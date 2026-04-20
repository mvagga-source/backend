package com.project.app.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.audition.dto.AuditionDto;

public interface AdminAuditionRepository extends JpaRepository<AuditionDto, Long> {

	// 전체 회차 목록 (회차 번호 오름차순)
	List<AuditionDto> findAllByIsDeletedFalseOrderByRoundAsc();
	
	// 회차별 참가자 목록 + 득표수 + 이름 (커트라인 판정용)
	// 반환: [IdolDto, voteCount, name]
    @Query("""
        SELECT i,
           (SELECT COUNT(vd.voteDetailId) FROM VoteDetailDto vd WHERE vd.idol = i)
           + (SELECT COALESCE(SUM(vb.bonusVotes), 0) FROM VoteBonusDto vb WHERE vb.idol = i AND vb.audition = i.audition)
           AS voteCount,
           p.name
        FROM IdolDto i
        LEFT JOIN IdolProfileDto p ON p.profileId = i.idolProfileId
        WHERE i.audition.auditionId = :auditionId
        ORDER BY (SELECT COUNT(vd.voteDetailId) FROM VoteDetailDto vd WHERE vd.idol = i)
    		   + (SELECT COALESCE(SUM(vb.bonusVotes), 0) FROM VoteBonusDto vb WHERE vb.idol = i AND vb.audition = i.audition) DESC
    """)
    List<Object[]> findIdolsWithVoteCount(@Param("auditionId") Long auditionId);
    
	// 특정 회차에서 이미 어떤 팀에든 배정된 idol ID 목록
    @Query("SELECT tm.idol.idolId FROM TeamMemberDto tm WHERE tm.team.audition.auditionId = :auditionId")
    List<Long> findAssignedIdolIdsByAuditionId(@Param("auditionId") Long auditionId);
}
