package com.project.app.admin.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.admin.service.AdminAuditionService;
import com.project.app.audition.dto.AuditionDto;

import lombok.RequiredArgsConstructor;

//테스트 AdminAuditionController대신 붙이기 클래스명 변경후(ajax나 fetch는 @ResponseBody를 추가, list이동페이지 추가)
/*@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AuditionController {

	private final AdminAuditionService adminAuditionService;
	
	@GetMapping("/audition/list")
	public String list() {
		return "/admin/audition/list";
	}
	
	// ── 회차 등록 ────────────────────────────────────
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
    
	// ── 회차 수정 ────────────────────────────────────
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
    
	// ── 상태 변경 ────────────────────────────────────
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
    
	// ── 참가자 목록 + 득표수 조회 (Ajax) ─────────────
    // GET /admin/audition/{id}/idols
	@ResponseBody
    @GetMapping("/audition/{id}/idols")
    public List<Object[]> getIdols(@PathVariable("id") Long auditionId) {
        return adminAuditionService.getIdolsWithVoteCount(auditionId);
    }
    
	// ── 탈락 처리 (단건) ─────────────────────────────
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
    
    // ── 탈락 취소 (단건) ─────────────────────────────
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
    
    // ── 커트라인 기준 일괄 탈락 처리 ─────────────────
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
    
    // ── 다음 회차 참가자 자동 생성 ───────────────────
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
}
*/