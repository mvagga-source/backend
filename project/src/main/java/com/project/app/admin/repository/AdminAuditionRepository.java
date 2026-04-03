package com.project.app.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.audition.dto.AuditionDto;

public interface AdminAuditionRepository extends JpaRepository<AuditionDto, Long> {

	// 전체 회차 목록 (회차 번호 오름차순)
	List<AuditionDto> findAllByOrderByRoundAsc();
	
	// 회차별 참가자 목록 + 득표수 + 이름 (커트라인 판정용)
	// 반환: [IdolDto, voteCount, name]
    @Query("""
        SELECT i, COUNT(vd.voteDetailId) AS voteCount, p.name
        FROM IdolDto i
        LEFT JOIN IdolProfileDto p ON p.profileId = i.idolProfileId
        LEFT JOIN VoteDetailDto vd ON vd.idol = i
        WHERE i.audition.auditionId = :auditionId
        GROUP BY i, p.name
        ORDER BY COUNT(vd.voteDetailId) DESC
    """)
    List<Object[]> findIdolsWithVoteCount(@Param("auditionId") Long auditionId);
}
