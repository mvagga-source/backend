package com.project.app.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.board.dto.Board;
import com.project.app.comment.dto.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	// 특정 게시글의 댓글을 최신순으로 페이징 (처음 로딩용)
	List<Comment> findByBoardBno(Long bno, Pageable pageable);

	// 마지막으로 본 cno보다 작은(이전) 데이터들을 가져옴 (최신순)
    List<Comment> findByBoardBnoAndCnoLessThan(Long bno, Long lastCno, Pageable pageable);

	public Board save(Board bdto);

	public void deleteById(Long bno);
}
