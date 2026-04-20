package com.project.app.admin.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.project.app.audition.dto.AuditionDto;
import com.project.app.audition.dto.TeamMatchResponseDto;

public interface AdminAuditionService {

	// ── 오디션관리 관련 ──────────────────────────────────
	// 전체 회차 목록 조회
    List<AuditionDto> getAuditionList();

    // 회차 단건 조회
    AuditionDto getAudition(Long auditionId);

    // 회차 등록
    void createAudition(AuditionDto auditionDto);

    // 회차 수정
    void updateAudition(Long auditionId, AuditionDto auditionDto);

	// 회차 수정
    void deleteAudition(Long auditionId);

    // 상태 변경 (upcoming→ongoing→ended)
    void updateStatus(Long auditionId, String status);

    // 회차별 참가자 목록 + 득표수 조회
    List<Object[]> getIdolsWithVoteCount(Long auditionId);

    // 탈락 처리 (단건)
    void eliminateIdol(Long idolId);

    // 탈락 취소 (단건)
    void restoreIdol(Long idolId);

    // 커트라인 기준 일괄 탈락 처리
    void eliminateByRank(Long auditionId);
    
	// 다음 회차 참가자 자동 생성 (현재 회차 생존자 → 다음 회차 idol INSERT)
    void createNextRoundIdols(Long currentAuditionId, Long nextAuditionId);
    
    // ── 팀경연 관련 ──────────────────────────────────
    
    // 회차별 팀경연 목록 조회 (관리자용)
    List<TeamMatchResponseDto> getTeamMatches(Long auditionId);
 
    // 팀 + 대결 등록 (이미지 URL 포함)
    void createTeamMatch(Long auditionId, String matchName,
                         String teamAName, String teamAImgUrl,
                         String teamBName, String teamBImgUrl);
 
    // 팀원 배정
    void addTeamMember(Long teamId, Long idolId);
 
    // 팀원 제거
    void removeTeamMember(Long teamMemberId);
 
    // 팀에 소속된 팀원 목록 조회
    List<Object[]> getTeamMembers(Long teamId);
 
    // 팀경연 결과 입력 → VoteBonus 자동 생성
    void submitMatchResult(Long matchId, Long winnerTeamId,
                           java.math.BigDecimal teamAScore,
                           java.math.BigDecimal teamBScore);
 
    // 팀 대표 이미지 업로드 → 저장 후 접근 URL 반환
    String uploadTeamImage(MultipartFile file);
    
	// 팀 정보 수정 (팀명, 대표 이미지 URL)
    void updateTeam(Long teamId, String teamName, String teamImgUrl);

    // 팀경연 결과 초기화 (done → pending + VoteBonus 삭제)
    void resetMatchResult(Long matchId);
    
	// 배정 가능한 참가자 목록 (해당 오디션에서 미배정 active idol만)
    List<Object[]> getAvailableIdols(Long auditionId, Long teamId);

    // 체크박스 선택 후 일괄 등록
    void addTeamMembersBulk(Long teamId, List<Long> idolIds);

	// ── 슈퍼계정 관련 ──────────────────────────────────
	// 슈퍼계정 투표 배율 조회
    int getSuperVoteMultiplier();

    // 슈퍼계정 투표 배율 변경
    void setSuperVoteMultiplier(int multiplier);

}
