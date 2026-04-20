package com.project.app.admin.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.admin.service.AdminAuditionService;
import com.project.app.audition.dto.AuditionDto;
import com.project.app.audition.dto.TeamMatchResponseDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuditionController {

	private final AdminAuditionService adminAuditionService;
	
	// ── AuditionDto → Map 변환 (JSP EL Boolean 오류 방지) ──
    private Map<String, Object> toMap(AuditionDto a) {
        Map<String, Object> map = new HashMap<>();
        map.put("auditionId",    a.getAuditionId());
        map.put("round",         a.getRound());
        map.put("title",         a.getTitle());
        map.put("startDate",     a.getStartDate());
        map.put("endDate",       a.getEndDate());
        map.put("maxVoteCount",  a.getMaxVoteCount());
        map.put("survivorCount", a.getSurvivorCount());
        map.put("hasTeamMatch",  Boolean.TRUE.equals(a.getHasTeamMatch()) ? "true" : "false");
        map.put("bonusVotes",     a.getBonusVotes());
        map.put("status",        a.getStatus());
        return map;
    }
    
    // ── 오디션 관리 페이지 렌더링 ────────────────────────
    // GET /admin/audition/round
    @GetMapping("/audition/round")
    public String auditionList(Model model) {
        List<Map<String, Object>> auditionList = adminAuditionService.getAuditionList()
            .stream()
            .map(this::toMap)
            .collect(Collectors.toList());
        model.addAttribute("auditionList", auditionList);
        return "admin/audition/round";
    }
    
    // ── 팀경연 관리 페이지 렌더링 ───────────────────────
    // ✅ 신규: GET /admin/audition/team?auditionId=3
    @GetMapping("/audition/team")
    public String auditionTeam(@RequestParam("auditionId") Long auditionId, Model model) {
        // 선택된 회차 정보
        AuditionDto audition = adminAuditionService.getAudition(auditionId);
        model.addAttribute("audition", toMap(audition));
 
        // 팀경연 목록
        List<TeamMatchResponseDto> matches = adminAuditionService.getTeamMatches(auditionId);
        model.addAttribute("matches", matches);
 
        // 이 회차 참가자 목록 (팀원 배정용)
        List<Object[]> idols = adminAuditionService.getIdolsWithVoteCount(auditionId);
        model.addAttribute("idols", idols);
 
        return "admin/audition/team";  // ✅ team.jsp
    }
    
	// ── 팀경연 목록 조회 (Ajax용) ────────────────────────
	// GET /admin/audition/{id}/matches
	@ResponseBody
	@GetMapping("/audition/{id}/matches")
	public List<TeamMatchResponseDto> getTeamMatches(@PathVariable("id") Long auditionId) {
	    return adminAuditionService.getTeamMatches(auditionId);
	}
 
    // ── 회차 등록 ────────────────────────────────────────
    // POST /admin/audition/create
    @ResponseBody
    @PostMapping("/audition/create")
    public String createAudition(AuditionDto auditionDto) {
        try {
            adminAuditionService.createAudition(auditionDto);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // ── 회차 수정 ────────────────────────────────────────
    // POST /admin/audition/{id}/update
    @ResponseBody
    @PostMapping("/audition/{id}/update")
    public String updateAudition(@PathVariable("id") Long auditionId,
                                  AuditionDto auditionDto) {
        try {
            adminAuditionService.updateAudition(auditionId, auditionDto);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

	// ── 회차 삭제 ────────────────────────────────────────
    // POST /admin/audition/{id}/delete
    @PostMapping("/audition/{auditionId}/delete")
    @ResponseBody
    public String deleteAudition(@PathVariable("auditionId") Long auditionId) {
        try {
            adminAuditionService.deleteAudition(auditionId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    // ── 상태 변경 ────────────────────────────────────────
    // POST /admin/audition/{id}/status
    @ResponseBody
    @PostMapping("/audition/{id}/status")
    public String updateStatus(@PathVariable("id") Long auditionId,
                               @RequestParam("status") String status) {
        try {
            adminAuditionService.updateStatus(auditionId, status);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    // ── 참가자 목록 + 득표수 조회 (Ajax) ─────────────────
    // GET /admin/audition/{id}/idols
    @ResponseBody
    @GetMapping("/audition/{id}/idols")
    public List<Object[]> getIdols(@PathVariable("id") Long auditionId) {
        return adminAuditionService.getIdolsWithVoteCount(auditionId);
    }

    // ── 탈락 처리 (단건) ──────────────────────────────────
    // POST /admin/idol/{id}/eliminate
    @ResponseBody
    @PostMapping("/idol/{id}/eliminate")
    public String eliminateIdol(@PathVariable("id") Long idolId) {
        try {
            adminAuditionService.eliminateIdol(idolId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // ── 탈락 취소 (단건) ──────────────────────────────────
    // POST /admin/idol/{id}/restore
    @ResponseBody
    @PostMapping("/idol/{id}/restore")
    public String restoreIdol(@PathVariable("id") Long idolId) {
        try {
            adminAuditionService.restoreIdol(idolId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // ── 커트라인 기준 일괄 탈락 처리 ─────────────────────
    // POST /admin/audition/{id}/eliminateByRank
    @ResponseBody
    @PostMapping("/audition/{id}/eliminateByRank")
    public String eliminateByRank(@PathVariable("id") Long auditionId) {
        try {
            adminAuditionService.eliminateByRank(auditionId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // ── 다음 회차 참가자 자동 생성 ───────────────────────
    // POST /admin/audition/{id}/nextRound?nextAuditionId=3
    @ResponseBody
    @PostMapping("/audition/{id}/nextRound")
    public String createNextRoundIdols(
            @PathVariable("id") Long currentAuditionId,
            @RequestParam("nextAuditionId") Long nextAuditionId) {
        try {
            adminAuditionService.createNextRoundIdols(currentAuditionId, nextAuditionId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // ════════════════════════════════════════════════
    // 팀경연 관련 API (✅ 전체 신규)
    // ════════════════════════════════════════════════
 
    // ── 팀 대표 이미지 업로드 ────────────────────────────
    // POST /admin/team/image/upload
    @ResponseBody
    @PostMapping("/team/image/upload")
    public String uploadTeamImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return "error: 파일이 비어 있어요.";
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/"))
                return "error: 이미지 파일만 업로드할 수 있어요.";
            return adminAuditionService.uploadTeamImage(file);
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
 
    // ── 팀 + 대결 등록 ──────────────────────────────────
    // POST /admin/audition/{id}/match/create
    @ResponseBody
    @PostMapping("/audition/{id}/match/create")
    public String createTeamMatch(
            @PathVariable("id") Long auditionId,
            @RequestParam("matchName")  String matchName,
            @RequestParam("teamAName")  String teamAName,
            @RequestParam(value = "teamAImgUrl", required = false, defaultValue = "") String teamAImgUrl,
            @RequestParam("teamBName")  String teamBName,
            @RequestParam(value = "teamBImgUrl", required = false, defaultValue = "") String teamBImgUrl) {
        try {
            adminAuditionService.createTeamMatch(
                    auditionId, matchName,
                    teamAName, teamAImgUrl,
                    teamBName, teamBImgUrl);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
 
    // ── 팀원 배정 ────────────────────────────────────────
    // POST /admin/team/{teamId}/member/add
    @ResponseBody
    @PostMapping("/team/{teamId}/member/add")
    public String addTeamMember(
            @PathVariable("teamId") Long teamId,
            @RequestParam("idolId") Long idolId) {
        try {
            adminAuditionService.addTeamMember(teamId, idolId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // 배정 가능한 참가자 목록 (해당 오디션에서 미배정 active idol만)
	// GET /admin/team/{teamId}/available-idols?auditionId=N
    @ResponseBody
    @GetMapping("/team/{teamId}/available-idols")
    public List<Object[]> getAvailableIdols(
            @PathVariable("teamId") Long teamId,
            @RequestParam("auditionId") Long auditionId) {
        return adminAuditionService.getAvailableIdols(auditionId, teamId);
    }

    // 체크박스 선택 후 일괄 등록
    // POST /admin/team/{teamId}/members/add-bulk
    @ResponseBody
    @PostMapping("/team/{teamId}/members/add-bulk")
    public String addTeamMembersBulk(
            @PathVariable("teamId") Long teamId,
            @RequestParam("idolIds") List<Long> idolIds) {
        try {
            adminAuditionService.addTeamMembersBulk(teamId, idolIds);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    // ── 팀원 제거 ────────────────────────────────────────
    // POST /admin/team/member/{teamMemberId}/remove
    @ResponseBody
    @PostMapping("/team/member/{teamMemberId}/remove")
    public String removeTeamMember(@PathVariable("teamMemberId") Long teamMemberId) {
        try {
            adminAuditionService.removeTeamMember(teamMemberId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
 
    // ── 팀원 목록 조회 ───────────────────────────────────
    // GET /admin/team/{teamId}/members
    @ResponseBody
    @GetMapping("/team/{teamId}/members")
    public List<Object[]> getTeamMembers(@PathVariable("teamId") Long teamId) {
        return adminAuditionService.getTeamMembers(teamId);
    }
 
    // ── 팀경연 결과 입력 → VoteBonus 자동 생성 ───────────
    // POST /admin/match/{matchId}/result
    @ResponseBody
    @PostMapping("/match/{matchId}/result")
    public String submitMatchResult(
            @PathVariable("matchId") Long matchId,
            @RequestParam("winnerTeamId") Long winnerTeamId,
            @RequestParam("teamAScore")   BigDecimal teamAScore,
            @RequestParam("teamBScore")   BigDecimal teamBScore) {
        try {
            adminAuditionService.submitMatchResult(matchId, winnerTeamId, teamAScore, teamBScore);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
	// ── 팀 정보 수정 (팀명, 대표 이미지 URL) ────────────────
	// POST /admin/team/{teamId}/update
	@ResponseBody
	@PostMapping("/team/{teamId}/update")
	public String updateTeam(
	        @PathVariable("teamId") Long teamId,
	        @RequestParam("teamName") String teamName,
	        @RequestParam(value = "teamImgUrl", required = false, defaultValue = "") String teamImgUrl) {
	    try {
	        adminAuditionService.updateTeam(teamId, teamName, teamImgUrl);
	        return "success";
	    } catch (Exception e) {
	        return "error: " + e.getMessage();
	    }
	}
	
	// ── 팀경연 결과 초기화 (done → pending + VoteBonus 삭제) ──
	// POST /admin/match/{matchId}/reset
	@ResponseBody
	@PostMapping("/match/{matchId}/reset")
	public String resetMatchResult(@PathVariable("matchId") Long matchId) {
	    try {
	        adminAuditionService.resetMatchResult(matchId);
	        return "success";
	    } catch (Exception e) {
	        return "error: " + e.getMessage();
	    }
	}
	
	// ── 슈퍼계정 투표 배율 조회 ─────────────────────────
	// GET /admin/super/multiplier
	@ResponseBody
	@GetMapping("/super/multiplier")
	public int getSuperVoteMultiplier() {
	    return adminAuditionService.getSuperVoteMultiplier();
	}

	// ── 슈퍼계정 투표 배율 변경 ─────────────────────────
	// POST /admin/super/multiplier?value=50
	@ResponseBody
	@PostMapping("/super/multiplier")
	public String setSuperVoteMultiplier(@RequestParam("value") int value) {
	    if (value < 1 || value > 1000) return "error: 1~1000 사이 값만 허용돼요.";
	    adminAuditionService.setSuperVoteMultiplier(value);
	    return "success";
	}
}
