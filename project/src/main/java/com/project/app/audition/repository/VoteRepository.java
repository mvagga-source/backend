package com.project.app.audition.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.audition.dto.AuditionDto;
import com.project.app.audition.dto.VoteDto;
import com.project.app.auth.dto.MemberDto;

public interface VoteRepository extends JpaRepository<VoteDto, Long> {

	// 오늘 이미 투표했는지 확인
	boolean existsByMemberAndAuditionAndVoteDate(
			MemberDto member, AuditionDto audition, LocalDate voteDate
			);
	
	// 오늘 투표한 아이돌 ID 목록 조회
	@Query("""
	    SELECT vd.idol.idolId
	    FROM VoteDto v
	    JOIN VoteDetailDto vd ON vd.vote = v
	    WHERE v.member.id = :memberId
	      AND v.audition.auditionId = :auditionId
	      AND v.voteDate = :voteDate
	""")
	List<Long> findVotedIdolIdsByMemberAndAuditionAndVoteDate(
	    @Param("memberId") String memberId,
	    @Param("auditionId") Long auditionId,
	    @Param("voteDate") LocalDate voteDate
	);
}
