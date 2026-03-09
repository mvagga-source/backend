package com.project.app.board.service;

import java.util.List;
import java.util.Map;

import com.project.app.board.dto.Board;
import com.project.app.user.dto.LoginRequest;
import com.project.app.user.dto.Member;

public interface BoardService {
	//전체
	public Map<String, Object> findAll(int page, int size, String category, String search);
}
