package com.project.app.audition.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.audition.dto.IdolDto;
import com.project.app.audition.dto.VoteRequestDto;
import com.project.app.audition.service.VoteService;
import com.project.app.auth.dto.MemberDto;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/audition")
@RequiredArgsConstructor
public class VoteController {

	private final VoteService  voteService;

    // ── 투표 대상 아이돌 목록 ──────────────────────────
    // GET /audition/idols?auditionId=2
    @GetMapping("/idols")
    public ResponseEntity<?> getIdols(@RequestParam Long auditionId) {
        try {
            List<IdolDto> idols = voteService.getActiveIdols(auditionId);
            return ResponseEntity.ok(idols);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── 오늘 투표 여부 확인 ────────────────────────────
    // GET /audition/vote/status?auditionId=2
    @GetMapping("/vote/status")
    public ResponseEntity<?> getVoteStatus(
            @RequestParam Long auditionId,
            HttpSession session) {
        try {
            // 세션에서 로그인한 회원 id 꺼내기
        	MemberDto member = (MemberDto) session.getAttribute("user");
            if (member == null) {
                return ResponseEntity.status(401).body("로그인이 필요해요.");
            }

            boolean hasVoted = voteService.hasVotedToday(member.getId(), auditionId);
            return ResponseEntity.ok(hasVoted);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── 투표 제출 ──────────────────────────────────────
    // POST /audition/vote
    // Body: { "auditionId": 2, "idolIds": [1, 2, 3, 4, 5, 6, 7] }
    @PostMapping("/vote")
    public ResponseEntity<?> submitVote(
            @RequestBody VoteRequestDto request,
            HttpSession session) {
        try {
            // 세션에서 로그인한 회원 id 꺼내기
        	MemberDto member = (MemberDto) session.getAttribute("user");
            if (member == null) {
                return ResponseEntity.status(401).body("로그인이 필요해요.");
            }

            voteService.submitVote(member.getId(), request);
            return ResponseEntity.ok("투표가 완료됐어요!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── 실시간 랭킹 ────────────────────────────────────
    // GET /audition/ranking?auditionId=2
    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking(@RequestParam Long auditionId) {
        try {
            List<Object[]> ranking = voteService.getRanking(auditionId);
            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
