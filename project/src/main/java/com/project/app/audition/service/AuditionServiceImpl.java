package com.project.app.audition.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.app.audition.dto.IdolResponseDto;
import com.project.app.audition.repository.AuditionRepository;
import com.project.app.audition.repository.IdolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditionServiceImpl implements AuditionService {

	private final IdolRepository idolRepository;
	private final AuditionRepository auditionRepository;
	
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
}
