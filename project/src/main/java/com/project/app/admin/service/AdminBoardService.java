package com.project.app.admin.service;

import com.project.app.board.dto.BoardDto;
import com.project.app.common.exception.BaCdException;

public interface AdminBoardService {
	public BoardDto findById(Long bno) throws BaCdException;
}
