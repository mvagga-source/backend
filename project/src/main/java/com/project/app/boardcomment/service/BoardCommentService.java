package com.project.app.boardcomment.service;

import java.util.List;
import java.util.Map;

import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;
import com.project.app.boardcomment.dto.BoardCommentDto;
import com.project.app.common.exception.BaCdException;

public interface BoardCommentService {
	public BoardCommentDto findById(BoardCommentDto cdto) throws BaCdException;

	public BoardCommentDto save(Long bno, MemberDto id, BoardCommentDto cdto) throws BaCdException;

	public Map<String, Object> findAll(int size, Long bno, Long lastCno) throws BaCdException;

	public BoardCommentDto update(BoardCommentDto cdto, MemberDto member) throws BaCdException;

	public BoardCommentDto deleteById(BoardCommentDto cdto, MemberDto member) throws BaCdException;
}
