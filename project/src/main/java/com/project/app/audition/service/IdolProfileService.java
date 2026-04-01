package com.project.app.audition.service;

import java.util.List;
import java.util.Map;

import com.project.app.audition.dto.IdolProfileDto;
import com.project.app.audition.dto.IdolResponseDto;
import com.project.app.common.exception.BaCdException;

public interface IdolProfileService {
    
    IdolResponseDto findIdolWithVote(Long auditionId, Long idolProfileId);

    // 프로필 가져오기
	IdolProfileDto findById(Long idolProfileId);

	// 프로필 깜빡임
	void updateMainImgUrl(Long idolProfileId, String fileName);

	public List<IdolProfileDto> findAll() throws BaCdException;

	// 하단 이미지
	Map<String, Object> getIdolDetail(Long profileId);
	
}