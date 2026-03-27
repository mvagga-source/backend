package com.project.app.qna.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.app.qna.dto.QnaDto;

import java.util.List;

public interface QnaRepository extends JpaRepository<QnaDto, Long> {
    
    // 내 문의 내역 보기 (삭제 안 된 것, 최신순)
	@Query("SELECT q FROM QnaDto q WHERE q.member.id = :memberId AND q.delYn = 'n' " +
			"AND q.qno < :lastQno ORDER BY q.qno DESC")
		Slice<QnaDto> findNextPage(@Param("memberId") String memberId, 
		                           @Param("lastQno") Long lastQno, 
		                           Pageable pageable);
	
	// 내 문의글 중 삭제되지 않은 전체 개수 카운트
    long countByMemberIdAndDelYn(String memberId, String delYn);
    
    // 관리자용: 답변 대기 중인 문의만 조회
    List<QnaDto> findByStatusAndDelYnOrderByCrdtAsc(String status, String delYn);
}