package com.project.app.audition.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.audition.dto.IdolResponseDto;
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
}
