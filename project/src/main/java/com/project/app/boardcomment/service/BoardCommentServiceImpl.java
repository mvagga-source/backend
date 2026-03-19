package com.project.app.boardcomment.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.project.app.common.exception.BaCdException;


@Service
@Transactional(rollbackFor = BaCdException.class)
public class BoardCommentServiceImpl implements BoardCommentService {

	@Autowired BoardCommentRepository commentRepository;

	@Autowired
	BoardRepository boardRepository;

	@Autowired
	MemberRepository memberRepository;

	@Transactional
	@Override
	public BoardCommentDto save(Long bno, MemberDto member, BoardCommentDto cdto) {
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
		return comment;		//Transactional 덕분에 save 호출 없이도 bgroup 업데이트 반영
	}

	@Override
	public void deleteById(BoardCommentDto cdto) {
		commentRepository.deleteById(cdto.getCno());
	}

	@Transactional
	@Override
	public BoardCommentDto findById(BoardCommentDto cdto) {
		BoardCommentDto Comment = commentRepository.findById(cdto.getCno()).orElseGet(()->{return null;});
		return Comment;
	}

	@Override
	public Map<String, Object> findAll(int size, Long bno, Long lastCno) {
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
	}
}
