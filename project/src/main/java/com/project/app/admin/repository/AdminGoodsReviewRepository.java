package com.project.app.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.app.goods.dto.GoodsDto;
import com.project.app.goodsreview.dto.GoodsReviewDto;

public interface AdminGoodsReviewRepository extends JpaRepository<GoodsReviewDto, Long> {
	// 소수점 2자리까지 반올림하여 조회
    @Query("SELECT ROUND(AVG(r.rating), 2) FROM GoodsReviewDto r WHERE r.delYn = 'n'")
    Double avgRating();

    /**
     * 차트
     * @return
     */
    @Query("""
        SELECT 
            SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END)
        FROM GoodsReviewDto r
    """)
    List<Long> ratingDistribution();
}
