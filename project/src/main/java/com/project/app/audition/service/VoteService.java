package com.project.app.audition.service;

import com.project.app.audition.dto.VoteRequestDto;

public interface VoteService {

	// 투표 제출
    void submitVote(String memberId, VoteRequestDto request);

    // 오늘 이미 투표했는지 확인
    boolean hasVotedToday(String memberId, Long auditionId);
    
}
