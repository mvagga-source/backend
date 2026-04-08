package com.project.app.admin.service;

import com.project.app.board.dto.BoardDto;
import com.project.app.boardcomment.dto.BoardCommentDto;
import com.project.app.common.exception.BaCdException;

public interface AdminBoardCommentService {
	public BoardCommentDto findById(Long cno) throws BaCdException;
}
