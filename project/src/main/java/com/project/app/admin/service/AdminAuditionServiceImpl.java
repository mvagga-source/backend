package com.project.app.admin.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.admin.repository.AdminAuditionRepository;
import com.project.app.audition.dto.AuditionDto;
import com.project.app.audition.dto.IdolDto;
import com.project.app.audition.dto.IdolProfileDto;
import com.project.app.audition.dto.TeamDto;
import com.project.app.audition.dto.TeamMatchDto;
import com.project.app.audition.dto.TeamMatchResponseDto;
import com.project.app.audition.dto.TeamMemberDto;
import com.project.app.audition.dto.VoteBonusDto;
import com.project.app.audition.repository.AuditionRepository;
import com.project.app.audition.repository.IdolProfileRepository;
import com.project.app.audition.repository.IdolRepository;
import com.project.app.audition.repository.TeamMatchRepository;
import com.project.app.audition.repository.TeamMemberRepository;
import com.project.app.audition.repository.TeamRepository;
import com.project.app.audition.repository.VoteBonusRepository;
import com.project.app.audition.service.VoteServiceImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAuditionServiceImpl implements AdminAuditionService {

	private final AdminAuditionRepository adminAuditionRepository;
	private final IdolRepository idolRepository;
	private final TeamRepository teamRepository;
	private final TeamMatchRepository teamMatchRepository;
	private final TeamMemberRepository teamMemberRepository;
	private final VoteBonusRepository voteBonusRepository;
	private final VoteServiceImpl voteServiceImpl;
	private final AuditionRepository auditionRepository;
	private final IdolProfileRepository idolProfileRepository;
	
    // application.properties 의 file.upload-dir 값 주입
    // ex) C:/upload/
    @Value("${file.upload-dir}")
    private String uploadBaseDir;
 
    @Value("${server.host}")
    private String serverHost;
 
    @Value("${server.port}")
    private String serverPort;
	

    // ════════════════════════════════════════════════
    // 오디션관리
    // ════════════════════════════════════════════════
    
	// ── 전체 회차 목록 조회 ──────────────────────────
	@Override
	public List<AuditionDto> getAuditionList() {
		return adminAuditionRepository.findAllByIsDeletedFalseOrderByRoundAsc();
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
        audition.setBonusVotes(form.getBonusVotes());
        audition.setSurvivorCount(form.getSurvivorCount());
        adminAuditionRepository.save(audition);
	}
	
	// ── 회차 삭제 ────────────────────────────────────
	@Override
	@Transactional
	public void deleteAudition(Long auditionId) {
	    AuditionDto audition = auditionRepository.findById(auditionId)
	        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다."));
	    audition.setIsDeleted(true);
	    auditionRepository.save(audition);
	}
	
	// ── 상태 변경 ────────────────────────────────────
	@Override
	@Transactional
	public void updateStatus(Long auditionId, String status) {
		AuditionDto audition = getAudition(auditionId);
        audition.setStatus(status);
        adminAuditionRepository.save(audition);
        
        // 1차 오디션을 ongoing으로 시작할 때 idol 자동 생성
        if ("ongoing".equals(status) && audition.getRound() == 1) {
        	//  SQL 데이터가 이미 있으면, idol 중복 생성 방지
            boolean alreadyExists = idolRepository.existsByAudition(audition);
            if (!alreadyExists) {
                List<IdolProfileDto> profiles = idolProfileRepository.findAll();
                for (IdolProfileDto profile : profiles) {
                    IdolDto idol = IdolDto.builder()
                        .audition(audition)
                        .idolProfileId(profile.getProfileId())
                        .status("active")
                        .build();
                    idolRepository.save(idol);
                }
            }
        }
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


    // ════════════════════════════════════════════════
    // 팀경연 관련
    // ════════════════════════════════════════════════
	
	// ── 팀 대표 이미지 업로드 ────────────────────────
    @Override
    public String uploadTeamImage(MultipartFile file) {
 
        // 실제 저장 경로 C:/upload/action profile/teamImages/
        // uploadBaseDir = "C:/upload/" (application.properties의 file.upload-dir)
        String teamUploadDir = uploadBaseDir + "action profile/teamImages/";
        File dir = new File(teamUploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
 
        // 원본 파일명에서 확장자만 추출
        String originalName = file.getOriginalFilename();
        String extension    = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
 
        // UUID로 고유 파일명 생성
        String savedFileName = UUID.randomUUID().toString() + extension;
 
        // 실제 파일 저장
        try {
            file.transferTo(new File(teamUploadDir + savedFileName));
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 중 오류가 발생했어요: " + e.getMessage());
        }
 
        // 서빙 URL도 /teamImages/ 로 맞춤 (WebConfig의 핸들러와 일치)
        return "/teamImages/" + savedFileName;
    }
    
    // ── 회차별 팀경연 목록 조회 (관리자용) ───────────
    @Override
    @Transactional(readOnly = true)
    public List<TeamMatchResponseDto> getTeamMatches(Long auditionId) {
        
    	List<TeamMatchDto> matches = teamMatchRepository
    			.findByAudition_AuditionIdOrderByMatchId(auditionId);
    	
    	List<TeamMatchResponseDto> result = new ArrayList<>();
    	
    	for (TeamMatchDto m : matches) {
    		List<String> membersA = teamMemberRepository
    				.findMemberNamesByTeamId(m.getTeamA().getTeamId());
    		List<String> membersB = teamMemberRepository
    				.findMemberNamesByTeamId(m.getTeamB().getTeamId());
    		
    		TeamMatchResponseDto dto = TeamMatchResponseDto.builder()
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
    		
    		result.add(dto);
    	}
    	return result;
    }
    
    // ── 팀 + 대결 등록 ──────────────────────────────
    @Override
    @Transactional
    public void createTeamMatch(Long auditionId, String matchName,
                                 String teamAName, String teamAImgUrl,
                                 String teamBName, String teamBImgUrl) {
        AuditionDto audition = getAudition(auditionId);
 
        TeamDto teamA = teamRepository.save(
                TeamDto.builder()
                        .audition(audition)
                        .teamName(teamAName)
                        .teamImgUrl(teamAImgUrl)
                        .build()
        );
        TeamDto teamB = teamRepository.save(
                TeamDto.builder()
                        .audition(audition)
                        .teamName(teamBName)
                        .teamImgUrl(teamBImgUrl)
                        .build()
        );
 
        teamMatchRepository.save(
                TeamMatchDto.builder()
                        .audition(audition)
                        .matchName(matchName)
                        .teamA(teamA)
                        .teamB(teamB)
                        .build()
        );
    }
    
    // ── 팀원 배정 ───────────────────────────────────
    @Override
    @Transactional
    public void addTeamMember(Long teamId, Long idolId) {
        TeamDto team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 팀이에요."));
        IdolDto idol = idolRepository.findById(idolId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 참가자예요."));
        boolean alreadyExists = teamMemberRepository
                .findByTeam_TeamId(teamId)
                .stream()
                .anyMatch(tm -> tm.getIdol().getIdolId().equals(idolId));
        if (alreadyExists) {
            throw new RuntimeException("이미 이 팀에 배정된 참가자예요.");
        }
        teamMemberRepository.save(
                TeamMemberDto.builder().team(team).idol(idol).build()
        );
    }
    
    @Override// 배정 가능한 참가자 목록 (해당 오디션에서 미배정 active idol만)
    public List<Object[]> getAvailableIdols(Long auditionId, Long teamId) {
        List<Long> assignedIds = adminAuditionRepository.findAssignedIdolIdsByAuditionId(auditionId);
        return adminAuditionRepository.findIdolsWithVoteCount(auditionId)
            .stream()
            .filter(row -> {
                com.project.app.audition.dto.IdolDto idol =
                    (com.project.app.audition.dto.IdolDto) row[0];
                return "active".equals(idol.getStatus())
                    && !assignedIds.contains(idol.getIdolId());
            })
            .collect(java.util.stream.Collectors.toList());
    }

    @Override// 체크박스 선택 후 일괄 등록
    @Transactional
    public void addTeamMembersBulk(Long teamId, List<Long> idolIds) {
        for (Long idolId : idolIds) {
            addTeamMember(teamId, idolId);  // 기존 단건 메서드 재사용
        }
    }
    
    // ── 팀원 제거 ───────────────────────────────────
    @Override
    @Transactional
    public void removeTeamMember(Long teamMemberId) {
        teamMemberRepository.deleteById(teamMemberId);
    }
 
    // ── 팀원 목록 조회 ──────────────────────────────
    @Override
    public List<Object[]> getTeamMembers(Long teamId) {
        return teamMemberRepository.findMembersWithIdolIdByTeamId(teamId);
    }
    
    // ── 팀경연 결과 입력 → VoteBonus 자동 생성 ───────
    @Override
    @Transactional
    public void submitMatchResult(Long matchId, Long winnerTeamId,
                                   BigDecimal teamAScore, BigDecimal teamBScore) {
        TeamMatchDto match = teamMatchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 대결이에요."));
        if ("done".equals(match.getStatus())) {
            throw new RuntimeException("이미 결과가 입력된 대결이에요.");
        }
        TeamDto winnerTeam = teamRepository.findById(winnerTeamId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 팀이에요."));
        match.setTeamAScore(teamAScore);
        match.setTeamBScore(teamBScore);
        match.setWinnerTeam(winnerTeam);
        match.setStatus("done");
        teamMatchRepository.save(match);
 
        AuditionDto audition = match.getAudition();
        Long bonusVotes = audition.getBonusVotes();
        List<TeamMemberDto> winners = teamMemberRepository
                .findByTeam_TeamId(winnerTeam.getTeamId());
        if (winners.isEmpty()) {
            throw new RuntimeException("승리팀에 배정된 팀원이 없어요.");
        }
        String reason = match.getMatchName() + " 팀경연 승리 (" + winnerTeam.getTeamName() + ")";
        for (TeamMemberDto member : winners) {
            voteBonusRepository.save(
                    VoteBonusDto.builder()
                            .audition(audition)
                            .teamMatch(match)
                            .idol(member.getIdol())
                            .bonusVotes(bonusVotes)
                            .reason(reason)
                            .build()
            );
        }
    }
    
	// ── 팀 정보 수정 (팀명, 대표 이미지 URL) ─────────────
    @Override
    @Transactional
    public void updateTeam(Long teamId, String teamName, String teamImgUrl) {
        TeamDto team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 팀이에요."));
        team.setTeamName(teamName);
        team.setTeamImgUrl(teamImgUrl);
        teamRepository.save(team);
    }

    // ── 팀경연 결과 초기화 (done → pending + VoteBonus 삭제) ──
    @Override
    @Transactional
    public void resetMatchResult(Long matchId) {
        TeamMatchDto match = teamMatchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 대결이에요."));
        if (!"done".equals(match.getStatus())) {
            throw new RuntimeException("확정된 결과가 없는 대결이에요.");
        }
        // vote_bonus 삭제 (이 match에 해당하는 것만)
        List<VoteBonusDto> bonuses = voteBonusRepository.findByTeamMatch_MatchId(matchId);
        voteBonusRepository.deleteAll(bonuses);

        // team_match 초기화
        match.setWinnerTeam(null);
        match.setTeamAScore(null);
        match.setTeamBScore(null);
        match.setStatus("pending");
        teamMatchRepository.save(match);
    }

	// 슈퍼계정 투표 배율 조회
    @Override
    public int getSuperVoteMultiplier() {
        return voteServiceImpl.getSuperVoteMultiplier();
    }
	// 슈퍼계정 투표 배율 변경
    @Override
    public void setSuperVoteMultiplier(int multiplier) {
        voteServiceImpl.setSuperVoteMultiplier(multiplier);
    }
}
