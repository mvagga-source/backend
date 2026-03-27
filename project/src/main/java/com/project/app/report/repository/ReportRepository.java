package com.project.app.report.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.app.report.dto.ReportDto;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportDto, Long> {
	
	// 전체 신고 내역 조회 (lastId보다 작은 것 10개)
    @Query("SELECT r FROM ReportDto r WHERE r.repono < :lastId ORDER BY r.repono DESC")
    Slice<ReportDto> findNextPageAll(@Param("lastId") Long lastId, Pageable pageable);

    // 전체 신고 개수
    long count();
    
    // 처리 상태별 신고 내역 조회 (예: '신고대기'인 것만 보기)
    List<ReportDto> findByStatusOrderByCrdtAsc(String status);
    
    // 특정 대상(게시글 등)에 대한 신고 내역 확인
    //List<ReportDto> findByTargetTypeAndTargetId(String targetType, Long targetId);
}