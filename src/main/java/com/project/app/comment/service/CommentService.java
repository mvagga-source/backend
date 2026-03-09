package com.project.app.comment.service;

import java.util.List;
import java.util.Map;

import com.project.app.board.dto.Board;
import com.project.app.comment.dto.Comment;
import com.project.app.user.dto.LoginRequest;
import com.project.app.user.dto.Member;

public interface CommentService {
	public Comment findById(Comment cdto);

	public Comment save(Long bno, String id, Comment cdto);

	public Map<String, Object> findAll(int size, Long bno, Long lastCno);

	public void deleteById(Comment cdto);
}
