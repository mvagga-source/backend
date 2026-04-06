package com.project.app.admin.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.project.app.admin.repository.AdminVideoRepository;
import com.project.app.video.dto.VideoDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminVideoServiceImpl implements AdminVideoService {

	private final AdminVideoRepository adminVideoRepository;
	
	@Transactional
	@Override
	public void save(Long id) {
		
		VideoDto videoDto = adminVideoRepository.findById(id).orElse(null);
		videoDto.setDeletedFlag("Y");
		videoDto.setDeletedAt(LocalDateTime.now());

		adminVideoRepository.save(videoDto);
	}

}
