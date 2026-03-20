package com.project.app.boardlike.service;

import java.util.List;
import java.util.Map;

import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;
import com.project.app.boardlike.dto.BoardLikeDto;
import com.project.app.common.exception.BaCdException;

public interface BoardLikeService {
	public Map<String, Object> save(BoardLikeDto lbdto, MemberDto member) throws BaCdException;

	public BoardLikeDto findById(BoardLikeDto bdto) throws BaCdException;

	public void delete(BoardLikeDto bdto, MemberDto member) throws BaCdException;

	public BoardLikeDto view(BoardLikeDto bdto) throws BaCdException;
}
