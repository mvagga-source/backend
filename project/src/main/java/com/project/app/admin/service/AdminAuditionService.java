package com.project.app.admin.service;

import java.util.List;

import com.project.app.audition.dto.AuditionDto;

public interface AdminAuditionService {

	// 전체 회차 목록 조회
    List<AuditionDto> getAuditionList();

    // 회차 단건 조회
    AuditionDto getAudition(Long auditionId);

    // 회차 등록
    void createAudition(AuditionDto auditionDto);

    // 회차 수정
    void updateAudition(Long auditionId, AuditionDto auditionDto);

    // 상태 변경 (upcoming→ongoing→ended)
    void updateStatus(Long auditionId, String status);

    // 회차별 참가자 목록 + 득표수 조회
    List<Object[]> getIdolsWithVoteCount(Long auditionId);

    // 탈락 처리 (단건)
    void eliminateIdol(Long idolId);

    // 탈락 취소 (단건)
    void restoreIdol(Long idolId);

    // 커트라인 기준 일괄 탈락 처리
    void eliminateByRank(Long auditionId);
    
	// 다음 회차 참가자 자동 생성 (현재 회차 생존자 → 다음 회차 idol INSERT)
    void createNextRoundIdols(Long currentAuditionId, Long nextAuditionId);
}
