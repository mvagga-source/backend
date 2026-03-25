package com.project.app.audition.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.audition.dto.IdolDto;
import com.project.app.audition.service.VoteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audition")
@RequiredArgsConstructor
public class AuditionController {

	private final VoteService voteService;
	

    // ── 투표 대상 아이돌 목록 ──────────────────────────
    // GET /audition/idols?auditionId=2
    @GetMapping("/idols")
    public ResponseEntity<?> getIdols(@RequestParam("auditionId") Long auditionId) {
        try {
            List<IdolDto> idols = voteService.getActiveIdols(auditionId);
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
            List<Object[]> ranking = voteService.getRanking(auditionId);
            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
