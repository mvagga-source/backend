package com.project.app.board.service;

import java.util.List;
import java.util.Map;

import com.project.app.board.dto.BoardDto;
import com.project.app.common.exception.BaCdException;

public interface BoardService {
	//전체
	public Map<String, Object> findAll(int page, int size, String category, String search) throws BaCdException;

	public BoardDto save(BoardDto bdto) throws BaCdException;

	public BoardDto findById(BoardDto bdto) throws BaCdException;

	public void deleteById(BoardDto bdto) throws BaCdException;
}
