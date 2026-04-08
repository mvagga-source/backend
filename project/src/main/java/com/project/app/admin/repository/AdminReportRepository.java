package com.project.app.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.idea.dto.IdeaDto;
import com.project.app.report.dto.ReportDto;

public interface AdminReportRepository extends JpaRepository<ReportDto, Long> {
	@Query("SELECT r FROM ReportDto r WHERE " +
		       "(:reportType IS NULL OR r.reportType = :reportType) AND " +
		       "(:status IS NULL OR r.status = :status) AND " +
		       "(:startDate IS NULL OR r.crdt >= :startDate) AND " +
		       "(:endDate IS NULL OR r.crdt <= :endDate) AND (" +
		       "   (:search IS NULL OR :search = '') OR " +
		       "   (:category = 'reason' AND r.reason LIKE %:search%) OR " +
		       "   (:category = 'mid' AND r.member.nickname LIKE %:search%) OR " +
		       "   ((:category IS NULL OR :category = '') AND " +
		       "       (r.reason LIKE %:search% OR r.member.nickname LIKE %:search%))" +
		       ")")
	    Page<ReportDto> findByFilters(
	            @Param("reportType") String reportType,
	            @Param("category") String category,
	            @Param("search") String search,
	            @Param("status") String status,
	            @Param("startDate") java.sql.Timestamp startDate,
	            @Param("endDate") java.sql.Timestamp endDate,
	            Pageable pageable);
}
