package com.project.app.audition.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.audition.dto.TeamMemberDto;

public interface TeamMemberRepository extends JpaRepository<TeamMemberDto, Long> {
	//팀에 소속된 멤버 이름 목록을 조회 - TeamCompetition에서 팀 구성 섹션에 멤버 이름을 표시할 때 사용

	// 팀 id로 소속 멤버 전체 조회
    List<TeamMemberDto> findByTeam_TeamId(Long teamId);
 
    // 팀 id로 멤버 이름 목록만 조회 (idol_profile join)
    // team_member(팀 소속 정보) → idol(아이돌id) → idol_profile(아이돌 이름)
    @Query("""
        SELECT p.name
        FROM TeamMemberDto tm
        JOIN IdolDto i ON i.idolId = tm.idol.idolId
        JOIN IdolProfileDto p ON p.profileId = i.idolProfileId
        WHERE tm.team.teamId = :teamId
        ORDER BY tm.teamMemberId
    """)
    List<String> findMemberNamesByTeamId(@Param("teamId") Long teamId);
    
    // 팀 id로 [teamMemberId, idolId, 이름] 조회 (관리자 팀원 배정 화면용)
    @Query("""
        SELECT tm.teamMemberId, i.idolId, p.name
        FROM TeamMemberDto tm
        JOIN IdolDto i ON i.idolId = tm.idol.idolId
        JOIN IdolProfileDto p ON p.profileId = i.idolProfileId
        WHERE tm.team.teamId = :teamId
        ORDER BY tm.teamMemberId
    """)
    List<Object[]> findMembersWithIdolIdByTeamId(@Param("teamId") Long teamId);
}
