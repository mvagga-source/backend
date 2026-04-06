package com.project.app.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.notice.dto.NoticeDto;

public interface AdminNoticeRepository extends JpaRepository<NoticeDto, Long> {
	@Query("""
		    SELECT n FROM NoticeDto n
		    WHERE n.delYn = 'n'
		      AND (:search IS NULL OR :search = '' OR
		           (:category = 'ntitle' AND n.ntitle LIKE CONCAT('%', :search, '%')) OR
		           (:category = 'ncontent' AND n.ncontent LIKE CONCAT('%', :search, '%')) OR
		           ((:category IS NULL OR :category = '') AND 
		               (n.ntitle LIKE CONCAT('%', :search, '%') OR n.ncontent LIKE CONCAT('%', :search, '%'))
		           )
		      )
		      AND (:startDate IS NULL OR :startDate = '' OR n.startDate >= :startDt)
		      AND (:endDate IS NULL OR :endDate = '' OR n.endDate <= :endDt)
		    ORDER BY n.isPinned DESC, n.crdt DESC
		""")
	    Page<NoticeDto> findAllNoticeWithFilter(
	            @Param("category") String category,
	            @Param("search") String search,
	            @Param("startDate") String startDate, // 원본 문자열 (체크용)
	            @Param("startDt") java.time.LocalDateTime startDt, // 변환된 날짜
	            @Param("endDate") String endDate, // 원본 문자열 (체크용)
	            @Param("endDt") java.time.LocalDateTime endDt, // 변환된 날짜
	            Pageable pageable);
}
