package com.project.app.audition.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.project.app.audition.dto.IdolMediaDto;
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

	
	// -------------------------------------관리자
	
	Map<String, Object> getProfileListForAdmin(int page);

	String upload(MultipartFile mainImgFile);

	IdolProfileDto save(IdolProfileDto profileDto);

	void saveMedia(IdolMediaDto media);
	
}