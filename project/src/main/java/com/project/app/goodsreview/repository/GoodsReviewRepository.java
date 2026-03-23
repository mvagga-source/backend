package com.project.app.goodsreview.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.goodsorders.dto.GoodsOrdersDto;
import com.project.app.goodsreview.dto.GoodsReviewDto;

public interface GoodsReviewRepository extends JpaRepository<GoodsReviewDto, Long> {

	// 1. 페이징 기준이 될 '원글'들만 가져오기
    // 조건: 부모가 없고(원글), 삭제되지 않았고, 문의가 아닌 글
	@Query(value = "SELECT grno FROM (" +
	       "  SELECT grno FROM goods_review " +
	       "  WHERE gno = :gno AND parent_grno IS NULL AND del_yn = 'n' " +
	       "  AND (:lastGrno = 0 OR grno < :lastGrno) " +
	       "  ORDER BY grno DESC" +
	       ") WHERE ROWNUM <= :size", nativeQuery = true)
	List<Long> findValidRootIds(@Param("gno") Long gno, 
	                            @Param("lastGrno") Long lastGrno, 
	                            @Param("size") int size);

	// 2. 위에서 뽑은 ID들과 '삭제된 글'을 모두 포함해서 조회
	@Query("SELECT r FROM GoodsReviewDto r " +
	       "WHERE r.grno IN :ids OR (r.parent.grno IN :ids) " +
	       "ORDER BY r.grno DESC")
	List<GoodsReviewDto> findAllByIds(@Param("ids") List<Long> ids);

	// 삭제되지 않은 리뷰의 평균 별점 계산
    @Query("SELECT COALESCE(ROUND(AVG(r.rating), 1), 0.0) AS rating FROM GoodsReviewDto r WHERE r.goods.gno = :gno AND r.delYn = 'n'")
    Double getAverageRatingByGno(@Param("gno") Long gno);

}
