package com.project.app.audition.service;

import java.util.List;

import com.project.app.audition.dto.IdolDto;
import com.project.app.audition.dto.VoteRequestDto;

public interface VoteService {

	// 투표 제출
    void submitVote(String memberId, VoteRequestDto request);

    // 오늘 이미 투표했는지 확인
    boolean hasVotedToday(String memberId, Long auditionId);

    // 투표 대상 아이돌 목록 조회
    List<IdolDto> getActiveIdols(Long auditionId);

    // 실시간 랭킹 조회
    List<Object[]> getRanking(Long auditionId);
}
