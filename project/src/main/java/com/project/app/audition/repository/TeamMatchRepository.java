package com.project.app.audition.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.TeamMatchDto;

public interface TeamMatchRepository extends JpaRepository<TeamMatchDto, Long> {

	// 특정 회차의 팀경연 목록을 DB에서 조회하는 쿼리를 추가
	// 회차별 팀경연 목록 조회
    List<TeamMatchDto> findByAudition_AuditionIdOrderByMatchId(Long auditionId);
    // findBy              → WHERE
    // Audition_AuditionId → audition.auditionId  ← 연관 엔티티 필드 접근 (언더스코어로 구분)
    //					   → Audition(객체명)_AuditionId(그 객체의 필드명)
    // OrderBy             → ORDER BY
    // MatchId             → match_id ASC
    
    boolean existsByAudition_AuditionIdAndStatus(Long auditionId, String status);
}
