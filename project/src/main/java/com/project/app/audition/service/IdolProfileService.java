package com.project.app.audition.service;

import java.util.List;

import com.project.app.audition.dto.IdolProfileDto;
import com.project.app.audition.dto.IdolResponseDto;
import com.project.app.common.exception.BaCdException;

public interface IdolProfileService {
    // 딱 이 한 줄만 있으면 됩니다. (세미콜론 ; 으로 끝내기)
    IdolResponseDto findIdolWithVote(Long auditionId, Long idolProfileId);

    // 프로필 가져오기
	IdolProfileDto findById(Long idolProfileId);

	// 프로필 깜빡임
	void updateMainImgUrl(Long idolProfileId, String fileName);

	public List<IdolProfileDto> findAll() throws BaCdException;
}