package com.project.app.audition.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.AuditionDto;

public interface AuditionRepository extends JpaRepository<AuditionDto, Long> {

	// 오디션 회차 목록을 DB에서 조회하는 쿼리를 추가
	// ended / ongoing 회차만 회차 순 조회 (IdolRanking, TeamCompetition 탭용)
    List<AuditionDto> findByStatusInOrderByRoundAsc(List<String> statuses);
    // findBy        → WHERE
    // StatusIn      → status IN (...)      ← 여러 값 중 하나
    // OrderBy       → ORDER BY
    // RoundAsc      → round ASC (오름차순)
}
