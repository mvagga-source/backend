package com.project.app.audition.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.audition.dto.AuditionDto;
import com.project.app.audition.dto.IdolDto;

public interface IdolRepository extends JpaRepository<IdolDto, Long> {

	// 1.해당 회차의 생존 아이돌 목록 조회 (탈락자 제외)
    List<IdolDto> findByAuditionAndStatus(AuditionDto audition, String status);

    // 2.실시간 랭킹 — 득표수 + 가산점 적용 후 내림차순
    @Query("""
        SELECT i, 
               COUNT(vd.voteDetailId) AS rawVotes,
               COALESCE(SUM(vb.bonusRate), 0) AS totalBonus,
               ROUND(COUNT(vd.voteDetailId) 
                     * (1 + COALESCE(SUM(vb.bonusRate), 0) / 100)
               ) AS finalVotes
        FROM IdolDto i
        LEFT JOIN VoteDetailDto vd ON vd.idol = i
        LEFT JOIN VoteBonusDto vb  ON vb.idol = i 
                                   AND vb.audition = i.audition
        WHERE i.audition.auditionId = :auditionId
          AND i.status = 'active'
        GROUP BY i
        ORDER BY finalVotes DESC
    """)
    List<Object[]> findRankingByAuditionId(@Param("auditionId") Long auditionId);
}
