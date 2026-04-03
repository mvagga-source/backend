package com.project.app.admin.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminAuditionRepository;
import com.project.app.audition.dto.AuditionDto;
import com.project.app.audition.dto.IdolDto;
import com.project.app.audition.repository.IdolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAuditionServiceImpl implements AdminAuditionService {

	private final AdminAuditionRepository adminAuditionRepository;
	private final IdolRepository idolRepository;
	
	// ── 전체 회차 목록 조회 ──────────────────────────
	@Override
	public List<AuditionDto> getAuditionList() {
		return adminAuditionRepository.findAllByOrderByRoundAsc();
	}

	// ── 회차 단건 조회 ───────────────────────────────
	@Override
	public AuditionDto getAudition(Long auditionId) {
		return adminAuditionRepository.findById(auditionId)
	            .orElseThrow(() -> new RuntimeException("존재하지 않는 회차예요."));
	}

	// ── 회차 등록 ────────────────────────────────────
	@Override
	@Transactional
	public void createAudition(AuditionDto auditionDto) {
		adminAuditionRepository.save(auditionDto);
	}

	// ── 회차 수정 ────────────────────────────────────
	@Override
	@Transactional
	public void updateAudition(Long auditionId, AuditionDto form) {
		AuditionDto audition = getAudition(auditionId);
        audition.setTitle(form.getTitle());
        audition.setRound(form.getRound());
        audition.setStartDate(form.getStartDate());
        audition.setEndDate(form.getEndDate());
        audition.setMaxVoteCount(form.getMaxVoteCount());
        audition.setHasTeamMatch(form.getHasTeamMatch());
        audition.setBonusRate(form.getBonusRate());
        audition.setSurvivorCount(form.getSurvivorCount());
        adminAuditionRepository.save(audition);
	}

	// ── 상태 변경 ────────────────────────────────────
	@Override
	@Transactional
	public void updateStatus(Long auditionId, String status) {
		AuditionDto audition = getAudition(auditionId);
        audition.setStatus(status);
        adminAuditionRepository.save(audition);
	}

	// ── 회차별 참가자 + 득표수 조회 ──────────────────
	@Override
	public List<Object[]> getIdolsWithVoteCount(Long auditionId) {
		return adminAuditionRepository.findIdolsWithVoteCount(auditionId);
	}

	// ── 탈락 처리 (단건) ─────────────────────────────
	@Override
	@Transactional
	public void eliminateIdol(Long idolId) {
		IdolDto idol = idolRepository.findById(idolId)
	            .orElseThrow(() -> new RuntimeException("존재하지 않는 참가자예요."));
        idol.setStatus("eliminated");
        idol.setEliminatedRound(idol.getAudition().getRound());			// 탈락 회차 기록
        idol.setEliminatedAt(new Timestamp(System.currentTimeMillis()));// 탈락 시각 기록
        idolRepository.save(idol);
	}

	// ── 탈락 취소 (단건) ─────────────────────────────
	@Override
	@Transactional
	public void restoreIdol(Long idolId) {
		IdolDto idol = idolRepository.findById(idolId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 참가자예요."));
        idol.setStatus("active");
        idol.setEliminatedRound(null);  // 탈락 기록 초기화
        idol.setEliminatedAt(null);
        idolRepository.save(idol);
	}

	// ── 커트라인 기준 일괄 탈락 처리 ─────────────────
	@Override
	@Transactional
	public void eliminateByRank(Long auditionId) {

		AuditionDto audition = getAudition(auditionId);

        // survivorCount 미설정 시 처리 불가
        if (audition.getSurvivorCount() == null) {
            throw new RuntimeException("커트라인이 설정되지 않았어요.");
        }

        List<Object[]> idolsWithVotes = adminAuditionRepository
            .findIdolsWithVoteCount(auditionId);
        
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 득표 순위 기준으로 survivorCount 이후는 탈락
        for (int i = 0; i < idolsWithVotes.size(); i++) {
            IdolDto idol = (IdolDto) idolsWithVotes.get(i)[0];
            if (i < audition.getSurvivorCount()) {
                idol.setStatus("active");
                idol.setEliminatedRound(null);  // 복구 시 초기화
                idol.setEliminatedAt(null);
            } else {
                idol.setStatus("eliminated");
                idol.setEliminatedRound(audition.getRound());  // 탈락 회차 기록
                idol.setEliminatedAt(now);                     // 탈락 시각 기록
            }
            idolRepository.save(idol);
        }
	}

	// ── 다음 회차 참가자 자동 생성 ───────────────────
	@Override
	@Transactional
	public void createNextRoundIdols(Long currentAuditionId, Long nextAuditionId) {

	    AuditionDto currentAudition = getAudition(currentAuditionId);
	    AuditionDto nextAudition    = getAudition(nextAuditionId);
	    
	    // 현재 회차가 ended 상태인지 확인
	    if (!"ended".equals(currentAudition.getStatus())) {
	        throw new RuntimeException("현재 회차가 아직 종료되지 않았어요.");
	    }

	    // 다음 회차에 이미 참가자가 있는지 확인 (중복 방지)
	    List<IdolDto> existing = idolRepository.findByAuditionAndStatus(nextAudition, "active");
	    if (!existing.isEmpty()) {
	        throw new RuntimeException("다음 회차에 이미 참가자가 등록되어 있어요.");
	    }

	    // 현재 회차 생존자 조회
	    List<IdolDto> survivors = idolRepository
	        .findByAuditionAndStatus(currentAudition, "active");

	    if (survivors.isEmpty()) {
	        throw new RuntimeException("현재 회차에 생존자가 없어요.");
	    }

	    // 생존자를 다음 회차 idol로 INSERT
	    for (IdolDto current : survivors) {
	        IdolDto next = IdolDto.builder()
	            .audition(nextAudition)
	            .idolProfileId(current.getIdolProfileId())
	            .status("active")
	            .build();
	        idolRepository.save(next);
	    }
	}

}
