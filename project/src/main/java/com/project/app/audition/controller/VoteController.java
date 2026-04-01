package com.project.app.audition.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.audition.dto.VoteRequestDto;
import com.project.app.audition.service.VoteService;
import com.project.app.auth.dto.MemberDto;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vote")
@RequiredArgsConstructor
public class VoteController {

	private final VoteService  voteService;

    // ── 오늘 투표 여부 확인 ────────────────────────────
    // GET /api/vote/status?auditionId=2
    @GetMapping("/status")
    public ResponseEntity<?> getVoteStatus(
            @RequestParam("auditionId") Long auditionId,
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
    
	// ── 오늘 투표한 아이돌 ID 목록 조회 ──────────────
	// GET /api/vote/today?auditionId=5
	@GetMapping("/today")
	public ResponseEntity<?> getVotedIdols(
	        @RequestParam("auditionId") Long auditionId,
	        HttpSession session) {
		try {
	        MemberDto member = (MemberDto) session.getAttribute("user");
	        if (member == null) {
	            return ResponseEntity.status(401).body("로그인이 필요해요.");
	        }
	
	        List<Long> idolIds = voteService.getVotedIdolIds(member.getId(), auditionId);
	        return ResponseEntity.ok(idolIds);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}

    // ── 투표 제출 ──────────────────────────────────────
    // POST /api/vote
    // Body: { "auditionId": 2, "idolIds": [1, 2, 3, 4, 5, 6, 7] }
    @PostMapping
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

}
