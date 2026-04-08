package com.project.app.admin.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.notice.dto.NoticeDto;
import com.project.app.qna.dto.QnaDto;

public interface AdminQnaRepository extends JpaRepository<QnaDto, Long> {
	@Query(value = "SELECT q FROM QnaDto q JOIN q.member m WHERE q.delYn = 'n' " +
	           "AND (:status IS NULL OR :status = '' OR q.status = :status) " +
	           "AND (:startDt IS NULL OR q.crdt >= :startDt) " +
	           "AND (:endDt IS NULL OR q.crdt <= :endDt) " +
	           "AND (" +
	           "  :search IS NULL OR :search = '' " +
	           "  OR (:category = 'qtitle' AND q.qtitle LIKE CONCAT('%', :search, '%')) " +
	           "  OR (:category = 'qcontent' AND q.qcontent LIKE CONCAT('%', :search, '%')) " +
	           "  OR (:category = 'mid' AND (m.id LIKE CONCAT('%', :search, '%') OR m.nickname LIKE CONCAT('%', :search, '%'))) " +
	           "  OR ((:category IS NULL OR :category = '') AND (" +
	           "       q.qtitle LIKE CONCAT('%', :search, '%') " +
	           "       OR q.qcontent LIKE CONCAT('%', :search, '%') " +
	           "       OR m.id LIKE CONCAT('%', :search, '%') " +
	           "       OR m.nickname LIKE CONCAT('%', :search, '%')" +
	           "     ))" +
	           ")"+
	           "ORDER BY q.crdt DESC",
	           countQuery = "SELECT COUNT(q) FROM QnaDto q JOIN q.member m WHERE q.delYn = 'n' " +
	           "AND (:status IS NULL OR :status = '' OR q.status = :status) " +
	           "AND (:startDt IS NULL OR q.crdt >= :startDt) " +
	           "AND (:endDt IS NULL OR q.crdt <= :endDt) " +
	           "AND (" +
	           "  :search IS NULL OR :search = '' " +
	           "  OR (:category = 'qtitle' AND q.qtitle LIKE CONCAT('%', :search, '%')) " +
	           "  OR (:category = 'qcontent' AND q.qcontent LIKE CONCAT('%', :search, '%')) " +
	           "  OR (:category = 'mid' AND (m.id LIKE CONCAT('%', :search, '%') OR m.nickname LIKE CONCAT('%', :search, '%'))) " +
	           "  OR ((:category IS NULL OR :category = '') AND (" +
	           "       q.qtitle LIKE CONCAT('%', :search, '%') " +
	           "       OR q.qcontent LIKE CONCAT('%', :search, '%') " +
	           "       OR m.id LIKE CONCAT('%', :search, '%') " +
	           "       OR m.nickname LIKE CONCAT('%', :search, '%')" +
	           "     ))" +
	           ")")
	    Page<QnaDto> findAllQnaWithFilter(
	            @Param("category") String category, 
	            @Param("search") String search, 
	            @Param("status") String status, 
	            @Param("startDt") LocalDateTime startDt, 
	            @Param("endDt") LocalDateTime endDt, 
	            Pageable pageable);
}
