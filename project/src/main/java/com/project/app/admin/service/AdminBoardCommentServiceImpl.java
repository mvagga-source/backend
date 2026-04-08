package com.project.app.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminBoardCommentRepository;
import com.project.app.admin.repository.AdminReportRepository;
import com.project.app.boardcomment.dto.BoardCommentDto;
import com.project.app.common.exception.BaCdException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminBoardCommentServiceImpl implements AdminBoardCommentService {
	
	private final AdminBoardCommentRepository adminBoardCommentRepository;
	
	@Override
	public BoardCommentDto findById(Long cno) throws BaCdException {
		return adminBoardCommentRepository.findById(cno).orElse(null);
	}

}
