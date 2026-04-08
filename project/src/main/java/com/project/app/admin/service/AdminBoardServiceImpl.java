package com.project.app.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminBoardRepository;
import com.project.app.board.dto.BoardDto;
import com.project.app.common.exception.BaCdException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminBoardServiceImpl implements AdminBoardService {
	private final AdminBoardRepository adminBoardRepository;
	
	@Override
	public BoardDto findById(Long bno) throws BaCdException {
		return adminBoardRepository.findById(bno).orElse(null);
	}

}
