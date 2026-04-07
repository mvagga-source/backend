package com.project.app.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.goodsreview.dto.GoodsReviewDto;
import com.project.app.idea.dto.IdeaDto;

public interface AdminIdeaRepository extends JpaRepository<IdeaDto, Long> {
	// 검색 필터 적용 (카테고리, 제목/내용 검색, 날짜 범위)
	@Query("SELECT i FROM IdeaDto i WHERE " +
	           "(:ideacategory IS NULL OR i.ideacategory = :ideacategory) AND " +
	           "(:startDate IS NULL OR i.crdt >= :startDate) AND " +
	           "(:endDate IS NULL OR i.crdt <= :endDate) AND (" +
	           "  (:searchType = 'ideatitle' AND i.ideatitle LIKE %:search%) OR " +
	           "  (:searchType = 'ideacontent' AND i.ideacontent LIKE %:search%) OR " +
	           "  (:searchType = 'member' AND i.member.nickname LIKE %:search%) OR " + // 작성자(닉네임) 검색
	           "  (:searchType IS NULL OR :searchType = '' OR (i.ideatitle LIKE %:search% OR i.ideacontent LIKE %:search%))" + // 전체 검색 기본값
	           ")")
	    Page<IdeaDto> findByFilters(
	            @Param("ideacategory") String ideacategory,
	            @Param("searchType") String searchType, // 'ideatitle', 'ideacontent', 'member' 중 하나
	            @Param("search") String search,
	            @Param("startDate") java.sql.Timestamp startDate,
	            @Param("endDate") java.sql.Timestamp endDate,
	            Pageable pageable);
}
