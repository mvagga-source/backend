package com.project.app.boardcomment.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.repository.MemberRepository;
import com.project.app.board.dto.BoardDto;
import com.project.app.board.repository.BoardRepository;
import com.project.app.boardcomment.dto.BoardCommentDto;
import com.project.app.boardcomment.repository.BoardCommentRepository;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.notification.dto.NotificationDto;


@Service
@Transactional(rollbackFor = BaCdException.class)
public class BoardCommentServiceImpl implements BoardCommentService {

	@Autowired BoardCommentRepository commentRepository;

	@Autowired
	BoardRepository boardRepository;

	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Transactional
	@Override
	public BoardCommentDto save(Long bno, MemberDto member, BoardCommentDto cdto) throws BaCdException {
		BoardDto board = boardRepository.findById(bno).orElse(null);
		//MemberDto member = memberRepository.findById(id).orElse(null);
		cdto.setBoard(board);
		cdto.setMember(member);
		// 1. 원댓글 (부모가 없는 경우)
        if (cdto.getCgroup() == null || cdto.getCgroup() == 0) {
            Long maxGroup = commentRepository.findMaxCgroup();
            cdto.setCgroup(maxGroup + 1);
            cdto.setCstep(0L);
            cdto.setCindent(0L);
        }
        // 2. 답글 (부모의 cgroup, cstep, cindent 정보를 가지고 옴)
        else {
            // 기존 답글들 순서 조정
            commentRepository.updateCstep(cdto.getCgroup(), cdto.getCstep());
            // 부모보다 한 칸 뒤, 한 칸 들여쓰기
            cdto.setCstep(cdto.getCstep() + 1);
            cdto.setCindent(cdto.getCindent() + 1);
        }
		BoardCommentDto comment = commentRepository.save(cdto);
		
		// 알림 대상: 게시글 작성자
	    MemberDto boardWriter = board.getMember();

	    // 본인이 쓴 글에 본인이 댓글 단 게 아닐 때만 알림 발생
	    if (!boardWriter.getId().equals(member.getId())) {
	    	NotificationDto eventData = NotificationDto.builder()
	                .member(boardWriter)		//받는사람
	                .sender(member)		//보내는 사람
	                .nocontent("'" + cdto.getBoard().getBtitle() + "'글에 새로운 댓글이 달렸습니다.")
	                .type("BOARD_COMMENT")
	                .url("/Community/BoardView/" + bno)		//url DB에 있는거나 공통에 있는걸로 사용하는게 좋고 또는 리액트에서 url 가져오기
	                .isRead("n")
	                .build();
            eventPublisher.publishEvent(eventData);
	    }
		return comment;		//Transactional 덕분에 save 호출 없이도 bgroup 업데이트 반영
	}

	// 수정 로직 (save와 분리하여 내용만 변경)
	@Transactional
    @Override
    public BoardCommentDto update(BoardCommentDto cdto, MemberDto member) throws BaCdException {
        BoardCommentDto dto = commentRepository.findById(cdto.getCno()).orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));
        if(!dto.getMember().getId().equals(member.getId())) throw new BaCdException(ErrorCode.AUTH_USER_NOT_MATCH);		//작성자가 맞는지 확인
        dto.setCcontent(cdto.getCcontent()); // 내용만 업데이트 (Dirty Checking 활용)
        return dto;
    }

	@Transactional
    @Override
    public BoardCommentDto deleteById(BoardCommentDto cdto, MemberDto member) throws BaCdException {
        BoardCommentDto dto = commentRepository.findById(cdto.getCno()).orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));
        if(!dto.getMember().getId().equals(member.getId())) throw new BaCdException(ErrorCode.AUTH_USER_NOT_MATCH);		//작성자가 맞는지 확인
        dto.setDelYn("y");	// 삭제 여부만 Y로 변경
		return dto;
    }

	@Transactional
	@Override
	public BoardCommentDto findById(BoardCommentDto cdto) throws BaCdException {
		BoardCommentDto Comment = commentRepository.findById(cdto.getCno()).orElseGet(()->{return null;});
		return Comment;
	}

	/*@Override
	public Map<String, Object> findAll(int size, Long bno, Long lastCno) throws BaCdException {
		// 1. 서비스 단에서 정렬 기준 정의 (최신순)
		Sort sort = Sort.by(Sort.Order.desc("cgroup"), Sort.Order.asc("cstep"));	//정렬
	    Pageable pageable = PageRequest.of(0, size, sort);
		List<BoardCommentDto> list;
		// Service에서 정렬 및 조회 로직 분기 처리
        if (lastCno == null || lastCno == 0) {
            // [최초 조회] 가장 최신글
            list = commentRepository.findByBoardBno(bno, pageable);
        } else {
            // [스크롤 or 더보기 조회] 마지막으로 본 cno보다 작은(과거) 데이터들을 가져옴
            list = commentRepository.findByBoardBnoAndCnoLessThan(bno, lastCno, pageable);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", list);
        map.put("isLast", list.size() < size); // 더 가져올 데이터가 있는지 판별
        return map;
	}*/

	@Override
	public Map<String, Object> findAll(int size, Long bno, Long lastGroup) {
	    // 원글 10개 기준 조회 (해당 그룹의 답글은 모두 포함됨)
	    List<BoardCommentDto> list = commentRepository.findCommentsByGroupPaging(bno, lastGroup, size);

	    Long totalCount = commentRepository.countByBoardBnoAndDelYnAndCindent(bno, "n", 0L);

	    // 이번에 가져온 데이터 중 가장 마지막 원글의 cgroup 번호를 찾음 (다음 페이징용)
	    Long nextLastGroup = 0L;
	    if (!list.isEmpty()) {
	        // 정렬이 cgroup DESC이므로 마지막 요소의 cgroup이 가장 작음
	        nextLastGroup = list.get(list.size() - 1).getCgroup();
	    }

	    Map<String, Object> map = new HashMap<>();
	    map.put("list", list);
	    map.put("totalCount", totalCount);
	    map.put("lastGroup", nextLastGroup); // 프론트로 다음 기준점 전달

	    // 원글(cindent=0)의 개수가 요청한 size보다 적으면 마지막 페이지
	    long rootCount = list.stream().filter(c -> c.getCindent() == 0).count();
	    map.put("isLast", rootCount < size);

	    return map;
	}
}
