package com.project.app.audition.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.AuditionDto;
import com.project.app.audition.dto.VoteDto;
import com.project.app.auth.dto.MemberDto;

public interface VoteRepository extends JpaRepository<VoteDto, Long> {

	// 오늘 이미 투표했는지 확인
	boolean existsByMemberAndAuditionAndVoteDate(
			MemberDto member, AuditionDto audition, LocalDate voteDate
			);
}
