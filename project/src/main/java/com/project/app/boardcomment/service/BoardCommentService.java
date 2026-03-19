package com.project.app.boardcomment.service;

import java.util.List;
import java.util.Map;

import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;
import com.project.app.boardcomment.dto.BoardCommentDto;

public interface BoardCommentService {
	public BoardCommentDto findById(BoardCommentDto cdto);

	public BoardCommentDto save(Long bno, MemberDto id, BoardCommentDto cdto);

	public Map<String, Object> findAll(int size, Long bno, Long lastCno);

	public void deleteById(BoardCommentDto cdto);
}
