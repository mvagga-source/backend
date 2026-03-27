package com.project.app.audition.service;

import java.util.List;

import com.project.app.audition.dto.IdolResponseDto;

public interface AuditionService {

    // 득표수 포함 아이돌 목록 조회
    List<IdolResponseDto> getIdolsWithVotes(Long auditionId);

    // 실시간 랭킹 조회
    List<Object[]> getRanking(Long auditionId);
    
	// 전체 참가자 조회 (탈락자 포함)
    List<IdolResponseDto> getAllIdolsWithVotes(Long auditionId);

	// 개인 프로필
	IdolResponseDto findIdolWithVote(Long auditionId, Long idolProfileId);
}
