package com.project.app.admin.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.admin.service.AdminAuditionService;
import com.project.app.audition.dto.AuditionDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuditionController {

	private final AdminAuditionService adminAuditionService;
	
	// ── 회차 등록 ────────────────────────────────────
    // POST /admin/audition/create
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
    @GetMapping("/audition/{id}/idols")
    public List<Object[]> getIdols(@PathVariable("id") Long auditionId) {
        return adminAuditionService.getIdolsWithVoteCount(auditionId);
    }
    
	// ── 탈락 처리 (단건) ─────────────────────────────
    // POST /admin/idol/{id}/eliminate
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
    @PostMapping("/audition/{id}/eliminateByRank")
    public String eliminateByRank(@PathVariable("id") Long auditionId) {
        try {
            adminAuditionService.eliminateByRank(auditionId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
}
