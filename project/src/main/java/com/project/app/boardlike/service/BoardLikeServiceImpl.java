package com.project.app.boardlike.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;
import com.project.app.board.repository.BoardRepository;
import com.project.app.boardcomment.repository.BoardCommentRepository;
import com.project.app.boardlike.dto.BoardLikeDto;
import com.project.app.boardlike.repository.BoardLikeRepository;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.notification.dto.NotificationDto;


@Service
@Transactional(rollbackFor = BaCdException.class)
public class BoardLikeServiceImpl implements BoardLikeService {

	@Autowired BoardLikeRepository boardLikeRepository;
	
	@Autowired BoardRepository boardRepository;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Transactional
	@Override
	public Map<String, Object> save(BoardLikeDto dto, MemberDto member) throws BaCdException {
		// 1. 게시글 정보 조회 (알림 발송 및 작성자 확인용)
	    BoardDto board = boardRepository.findById(dto.getBoard().getBno())
	            .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));
	    
		Optional<BoardLikeDto> existingLike =boardLikeRepository.findByBoardBnoAndMemberId(dto.getBoard().getBno(), member.getId());

	    Integer myLike = null;

	    if (existingLike.isPresent()) {
	        BoardLikeDto current = existingLike.get();

	        if (current.getIsLike().equals(dto.getIsLike())) {
	            boardLikeRepository.delete(current);
	            myLike = null; // 취소
	        } else {
	            current.setIsLike(dto.getIsLike());
	            boardLikeRepository.save(current);
	            myLike = dto.getIsLike(); // 변경
	        }

	    } else {
	        dto.setMember(member);
	        boardLikeRepository.save(dto);
	        myLike = dto.getIsLike(); // 등록
	    }
	    
	    // [알림 로직 추가]
	    // 1. 결과가 '추천(1)'이고 2. 내 글이 아닐 때만 알림 발생
	    if (myLike != null && myLike == 1 && !board.getMember().getId().equals(member.getId())) {
	        NotificationDto eventData = NotificationDto.builder()
	                .member(board.getMember()) // 게시글 작성자 (수신자)
	                .sender(member)            // 추천 누른 사람 (발신자)
	                .nocontent(member.getNickname()+"님이 내 글'" + board.getBtitle() + "'을 추천했습니다.")
	                .type("BOARD_LIKE")        // 서비스에서 설정 체크할 타입
	                .url("/Community/BoardView/" + dto.getBoard().getBno())
	                .isRead("n")
	                .build();

	        eventPublisher.publishEvent(eventData);
	    }
	    boardRepository.flush();
	    Map<String, Object> result = new HashMap<>();
	    result.put("myLike", myLike);
	    return result;
	}

	@Override
	public BoardLikeDto view(BoardLikeDto dto) throws BaCdException {
		BoardLikeDto boardLikeDto = boardLikeRepository.findById(dto.getLbno()).orElseGet(()->{return null;});
		return boardLikeDto;
	}

	@Override
	public BoardLikeDto findById(BoardLikeDto dto) throws BaCdException {
		BoardLikeDto boardLikeDto = boardLikeRepository.findById(dto.getLbno()).orElseGet(()->{return null;});
		return boardLikeDto;
	}

	@Transactional
	@Override
	public void delete(BoardLikeDto dto, MemberDto member) throws BaCdException {
		BoardLikeDto boardLikeDto = boardLikeRepository.findById(dto.getLbno()).orElseGet(()->{return null;});
	}
}
