package com.project.app.audition.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.app.audition.dto.AuditionDto;
import com.project.app.audition.dto.AuditionResponseDto;
import com.project.app.audition.dto.IdolResponseDto;
import com.project.app.audition.dto.TeamMatchResponseDto;
import com.project.app.audition.repository.AuditionRepository;
import com.project.app.audition.repository.IdolRepository;
import com.project.app.audition.repository.TeamMatchRepository;
import com.project.app.audition.repository.TeamMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditionServiceImpl implements AuditionService {

	private final IdolRepository idolRepository;
	private final AuditionRepository auditionRepository;
	private final TeamMatchRepository teamMatchRepository;
	private final TeamMemberRepository teamMemberRepository;
	
    // ── 득표수 포함 아이돌 목록 조회 ─────────────────────
    @Override
    public List<IdolResponseDto> getIdolsWithVotes(Long auditionId) {

    	// 오디션 존재 여부 확인
    	auditionRepository.findById(auditionId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 오디션이에요."));

        // vote_detail 집계 포함 query
        return idolRepository.findIdolsWithVotes(auditionId);
    }

    // ── 실시간 랭킹 조회 ───────────────────────────────
    @Override
    public List<Object[]> getRanking(Long auditionId) {
    	
    	// 오디션 존재 여부 확인
        auditionRepository.findById(auditionId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 오디션이에요."));
        
        return idolRepository.findRankingByAuditionId(auditionId);
    }
    
	// ── 전체 참가자 조회 ───────────────────────────────
    @Override
    public List<IdolResponseDto> getAllIdolsWithVotes(Long auditionId) {
        auditionRepository.findById(auditionId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 오디션이에요."));
        return idolRepository.findAllIdolsWithVotes(auditionId);
    }

    // ── IdolList용 — 전체 참가자 최신 회차 status 포함 ────
    @Override
    public List<IdolResponseDto> getAllIdolsLatest() {
    	return idolRepository.findAllIdolsLatestStatus();
    }

	// ── 회차 목록 조회 (경연결과 페이지 전용) ───────────────────────────────
	@Override
	public List<AuditionResponseDto> getAuditionList() {
		List<AuditionResponseDto> result = new ArrayList<>();
 
		List<?> auditions = auditionRepository
	            .findByStatusInAndIsDeletedFalseOrderByRoundAsc(List.of("ended", "ongoing", "upcoming"));
 
		for (Object obj : auditions) {
			com.project.app.audition.dto.AuditionDto a =
				(com.project.app.audition.dto.AuditionDto) obj;
 
			result.add(AuditionResponseDto.builder()
				.auditionId(a.getAuditionId())
				.round(a.getRound())
				.title(a.getTitle())
				.startDate(a.getStartDate())
				.endDate(a.getEndDate())
				.status(a.getStatus())
				.hasTeamMatch(a.getHasTeamMatch())
				.hasMatchDone(teamMatchRepository.existsByAudition_AuditionIdAndStatus(a.getAuditionId(), "done"))  // 추가
				.survivorCount(a.getSurvivorCount())
				.build());
		}
		return result;
	}
	
	// ── 전체 회차 목록 조회 (Sidebar용) ──
	@Override
	public List<AuditionResponseDto> getAllAuditionList() {
	    List<AuditionResponseDto> result = new ArrayList<>();

	    List<AuditionDto> auditions = auditionRepository.findAllByIsDeletedFalseOrderByRoundAsc();

	    for (AuditionDto a : auditions) {
	        result.add(AuditionResponseDto.builder()
	            .auditionId(a.getAuditionId())
	            .round(a.getRound())
	            .title(a.getTitle())
	            .startDate(a.getStartDate())
	            .endDate(a.getEndDate())
	            .status(a.getStatus())
	            .hasTeamMatch(a.getHasTeamMatch())
	            .survivorCount(a.getSurvivorCount())
	            .build());
	    }
	    return result;
	}

	// ── 팀경연 결과 조회 ──────────────────────────────
	@Override
	public List<TeamMatchResponseDto> getMatches(Long auditionId) {
		auditionRepository.findById(auditionId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 오디션이에요."));

	    return teamMatchRepository
	        .findByAudition_AuditionIdOrderByMatchId(auditionId)
	        .stream()
	        .map(m -> {
	            // 팀원 이름 목록 조회
	            List<String> membersA = teamMemberRepository
	                .findMemberNamesByTeamId(m.getTeamA().getTeamId());
	            List<String> membersB = teamMemberRepository
	                .findMemberNamesByTeamId(m.getTeamB().getTeamId());
	
	            return TeamMatchResponseDto.builder()
	                .matchId(m.getMatchId())
	                .matchName(m.getMatchName())
	                .teamAId(m.getTeamA().getTeamId())
	                .teamAName(m.getTeamA().getTeamName())
	                .teamAImgUrl(m.getTeamA().getTeamImgUrl())
	                .teamBId(m.getTeamB().getTeamId())
	                .teamBName(m.getTeamB().getTeamName())
	                .teamBImgUrl(m.getTeamB().getTeamImgUrl())
	                .teamAScore(m.getTeamAScore())
	                .teamBScore(m.getTeamBScore())
	                .winnerTeamId(m.getWinnerTeam() != null ? m.getWinnerTeam().getTeamId() : null)
	                .status(m.getStatus())
	                .membersA(membersA)
	                .membersB(membersB)
	                .build();
	        })
	        .collect(Collectors.toList());
	}

}
