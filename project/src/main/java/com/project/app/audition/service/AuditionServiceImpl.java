package com.project.app.audition.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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

    // ── 개인 프로필 ───────────────────────────────
	@Override
	public IdolResponseDto findIdolWithVote(Long auditionId, Long idolProfileId) {
		// TODO Auto-generated method stub
		return null;
	}

	// ── 회차 목록 조회 ───────────────────────────────
	@Override
	public List<AuditionResponseDto> getAuditionList() {
		return auditionRepository
            .findByStatusInOrderByRoundAsc(List.of("ended", "ongoing"))
            .stream()
            // 리스트를 스트림(흐름)으로 변환
            // → AuditionDto1 → AuditionDto2 → AuditionDto3 (순서대로 하나씩 처리)
            .map(a -> AuditionResponseDto.builder()
                .auditionId(a.getAuditionId())
                .round(a.getRound())
                .title(a.getTitle())
                .startDate(a.getStartDate())
                .endDate(a.getEndDate())
                .status(a.getStatus())
                .hasTeamMatch(a.getHasTeamMatch())
                .build())
            	// 각 AuditionDto(a)를 AuditionResponseDto로 변환
            .collect(Collectors.toList());
			// 변환된 것들을 다시 리스트로 묶기
		    // → [AuditionResponseDto1, AuditionResponseDto2, AuditionResponseDto3]
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
	                .teamBId(m.getTeamB().getTeamId())
	                .teamBName(m.getTeamB().getTeamName())
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
