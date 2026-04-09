package com.project.app.audition.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.audition.dto.AuditionResponseDto;
import com.project.app.audition.dto.IdolResponseDto;
import com.project.app.audition.dto.TeamMatchResponseDto;
import com.project.app.audition.service.AuditionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audition")
@RequiredArgsConstructor
public class AuditionController {

	private final AuditionService auditionService;

    // ── 득표수 포함 아이돌 목록 ──────────────────────────
    // GET /api/audition/idols?auditionId=2
    @GetMapping("/idols")
    public ResponseEntity<?> getIdols(@RequestParam("auditionId") Long auditionId) {
        try {
            List<IdolResponseDto> idols = auditionService.getIdolsWithVotes(auditionId);
            return ResponseEntity.ok(idols);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // ── 실시간 랭킹 ────────────────────────────────────
    // GET /audition/ranking?auditionId=2
    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking(@RequestParam("auditionId") Long auditionId) {
        try {
            List<Object[]> ranking = auditionService.getRanking(auditionId);
            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 전체 참가자 조회 (탈락자 포함)
    // GET /api/audition/allIdols?auditionId=1
    @GetMapping("/allIdols")
    public ResponseEntity<?> getAllIdols(@RequestParam("auditionId") Long auditionId) {
    	try {
    		List<IdolResponseDto> idols = auditionService.getAllIdolsWithVotes(auditionId);
    		return ResponseEntity.ok(idols);
    	} catch (Exception e) {
    		return ResponseEntity.badRequest().body(e.getMessage());
    	}
    }
    
	// ── IdolList용 — 전체 참가자 최신 회차 status 포함 ──
	// GET /api/audition/allIdolsLatest
	@GetMapping("/allIdolsLatest")
	public ResponseEntity<?> getAllIdolsLatest() {
	    try {
	        List<IdolResponseDto> idols = auditionService.getAllIdolsLatest();
	        return ResponseEntity.ok(idols);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}

    // 회차 목록 조회
    // GET /api/audition/list
    @GetMapping("/list")
    public ResponseEntity<?> getAuditionList() {
        try {
            List<AuditionResponseDto> list = auditionService.getAuditionList();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
	// 전체 회차 목록 조회 (upcoming 포함 — Sidebar용)
	// GET /api/audition/all
	@GetMapping("/all")
	public ResponseEntity<?> getAllAuditionList() {
	    try {
	        List<AuditionResponseDto> list = auditionService.getAllAuditionList();
	        return ResponseEntity.ok(list);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}

    // 팀경연 결과 조회
    // GET /api/audition/matches?auditionId=2
    @GetMapping("/matches")
    public ResponseEntity<?> getMatches(@RequestParam("auditionId") Long auditionId) {
        try {
            List<TeamMatchResponseDto> matches = auditionService.getMatches(auditionId);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}
