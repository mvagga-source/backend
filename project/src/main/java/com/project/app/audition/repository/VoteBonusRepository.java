package com.project.app.audition.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.VoteBonusDto;

public interface VoteBonusRepository extends JpaRepository<VoteBonusDto, Long> {

	// match_id로 vote_bonus 목록 조회
	List<VoteBonusDto> findByTeamMatch_MatchId(Long matchId);
}
