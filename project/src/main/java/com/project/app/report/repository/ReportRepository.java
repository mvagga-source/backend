package com.project.app.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.app.report.dto.ReportDto;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportDto, Long> {
    
    // 처리 상태별 신고 내역 조회 (예: 'waiting'인 것만 보기)
    List<ReportDto> findByStatusOrderByCrdtAsc(String status);
    
    // 특정 대상(게시글 등)에 대한 신고 내역 확인
    List<ReportDto> findByTargetTypeAndTargetId(String targetType, Long targetId);
}