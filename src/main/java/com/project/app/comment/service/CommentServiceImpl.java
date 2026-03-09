package com.project.app.comment.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.board.dto.Board;
import com.project.app.board.repository.BoardRepository;
import com.project.app.comment.dto.Comment;
import com.project.app.comment.repository.CommentRepository;
import com.project.app.user.dto.LoginRequest;
import com.project.app.user.dto.Member;
import com.project.app.user.repository.MemberRepository;


@Service
public class CommentServiceImpl implements CommentService {

	@Autowired CommentRepository commentRepository;

	@Autowired
	BoardRepository boardRepository;

	@Autowired
	MemberRepository memberRepository;

	@Transactional
	@Override
	public Comment save(Long bno, String id, Comment cdto) {
		Board board = boardRepository.findById(bno).orElse(null);
		Member member = memberRepository.findById(id).orElse(null);
		cdto.setBoard(board);
		cdto.setMember(member);
		Comment comment = commentRepository.save(cdto);
		return comment;		//Transactional 덕분에 save 호출 없이도 bgroup 업데이트 반영
	}

	@Override
	public void deleteById(Comment cdto) {
		commentRepository.deleteById(cdto.getCno());
	}

	@Transactional
	@Override
	public Comment findById(Comment cdto) {
		Comment Comment = commentRepository.findById(cdto.getCno()).orElseGet(()->{return null;});
		return Comment;
	}

	@Override
	public Map<String, Object> findAll(int size, Long bno, Long lastCno) {
		// 1. 서비스 단에서 정렬 기준 정의 (최신순)
	    Sort sort = Sort.by(Sort.Direction.DESC, "cno");
	    Pageable pageable = PageRequest.of(0, size, sort);
		List<Comment> list;
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
