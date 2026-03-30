package com.project.app.goodsreview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.goodsorders.dto.GoodsOrdersDto;
import com.project.app.goodsreview.dto.GoodsReviewDto;

public interface GoodsReviewRepository extends JpaRepository<GoodsReviewDto, Long> {

	// 1. 더보기 기준이 될 '원글'들만 가져오기
    // 조건: 부모가 없고(원글), 삭제되지 않았고, 문의가 아닌 글
	@Query(value = 
		    "SELECT grno FROM (" +
		    "  SELECT r.grno, r.rating, " +
		    "         (SELECT COUNT(*) FROM goods_review_like l WHERE l.grno = r.grno) as l_cnt " +
		    "  FROM goods_review r " +
		    "  WHERE r.gno = :gno AND r.parent_grno IS NULL " +
		    "  AND (" +
		    "    :lastGrno = 0 OR " +
		    "    CASE " +
		    "      WHEN :sortDir = 'like' THEN " +
		    "        CASE WHEN (SELECT COUNT(*) FROM goods_review_like l WHERE l.grno = r.grno) < :lastLikeCnt THEN 1 " +
		    "             WHEN (SELECT COUNT(*) FROM goods_review_like l WHERE l.grno = r.grno) = :lastLikeCnt AND r.grno < :lastGrno THEN 1 " +
		    "             ELSE 0 END " +
		    "      WHEN :sortDir = 'rating' THEN " +
		    "        CASE WHEN r.rating < :lastRating THEN 1 " +
		    "             /* 오차 범위를 직접 비교 (ROUND보다 안전할 수 있음) */ " +
		    "             WHEN ABS(r.rating - :lastRating) < 0.01 AND r.grno < :lastGrno THEN 1 " +
		    "             ELSE 0 END " +
		    "      ELSE " +
		    "        CASE WHEN r.grno < :lastGrno THEN 1 ELSE 0 END " +
		    "    END = 1" +
		    "  ) " +
		    "  ORDER BY " +
		    "    CASE WHEN :sortDir = 'like' THEN (SELECT COUNT(*) FROM goods_review_like l WHERE l.grno = r.grno) END DESC, " +
		    "    CASE WHEN :sortDir = 'rating' THEN r.rating END DESC, " +
		    "    r.grno DESC" +
		    ") WHERE ROWNUM <= :limit", nativeQuery = true)
	    List<Long> findIdsInRange(@Param("gno") Long gno, 
	                              @Param("lastGrno") Long lastGrno, 
	                              @Param("lastLikeCnt") long lastLikeCnt,
	                              @Param("lastRating") double lastRating,
	                              @Param("sortDir") String sortDir, 
	                              @Param("limit") int limit);

	 // 2. 추출된 ID들에 해당하는 리뷰 상세 정보 조회
	 // 정렬은 이미 Native Query(findIdsInRange)에서 끝났으므로, 
	 // 여기서는 가져온 데이터를 ID 순서대로만 정렬해주면 됩니다.
	 @Query("SELECT DISTINCT r FROM GoodsReviewDto r " +
	        "LEFT JOIN FETCH r.children " +
	        "WHERE r.grno IN :ids " +
	        "ORDER BY r.grno DESC") // 기본 정렬만 남깁니다. (상세 정렬은 서비스에서 처리)
	 List<GoodsReviewDto> findAllByGrnoIn(@Param("ids") List<Long> ids);
	 
	// 1. 더보기 기준이 될 '원글'들만 가져오기
    // 조건: 부모가 없고(원글), 삭제되지 않았고, 문의가 아닌 글
	/*@Query(value = "SELECT MIN(grno) FROM (" +
		       "  SELECT grno FROM goods_review " +
		       "  WHERE gno = :gno AND parent_grno IS NULL AND del_yn = 'n' " +
		       "  AND (:lastGrno = 0 OR grno < :lastGrno) " +
		       "  ORDER BY grno DESC" +
		       ") WHERE ROWNUM <= :size", nativeQuery = true)
	public Long findTargetGrno(@Param("gno") Long gno, 
		                    @Param("lastGrno") Long lastGrno, 
		                    @Param("size") int size);

	// 2. 위에서 뽑은 ID들과 '삭제된 글'을 모두 포함해서 조회
	@Query("SELECT r FROM GoodsReviewDto r " +
		       "LEFT JOIN FETCH r.children " +
		       "WHERE r.goods.gno = :gno " +
		       "AND r.grno >= :targetGrno " + // 찾은 10번째 정상 ID보다 크거나 같은 모든 글
		       "AND (:lastGrno = 0 OR r.grno < :lastGrno) " +
		       "AND r.parent IS NULL " + // 메인 리스트용 (답글은 children으로 딸려옴)
		       "ORDER BY r.grno DESC")
		List<GoodsReviewDto> findAllInRange(@Param("gno") Long gno, 
		                                    @Param("targetGrno") Long targetGrno,
		                                    @Param("lastGrno") Long lastGrno);*/
	
	// 총 개수 (삭제되지 않은 원글 + 답글 제외)
	@Query("SELECT COUNT(r) FROM GoodsReviewDto r " +
		       "WHERE r.goods.gno = :gno AND r.parent IS NULL AND r.delYn = 'n'")
	public Long totCntGoodsReview(@Param("gno") Long gno);

	// 삭제되지 않은 리뷰의 평균 별점 계산
    @Query("SELECT COALESCE(ROUND(AVG(r.rating), 1), 0.0) AS rating FROM GoodsReviewDto r WHERE r.goods.gno = :gno AND r.delYn = 'n'")
    public Double getAverageRatingByGno(@Param("gno") Long gno);
    
    //이미 주문내역에 해당하는 리뷰등록 되었는지 확인
    boolean existsByOrder_Gono(Long gono);
    
	// 주문 번호(Order 객체의 gono)와 삭제 여부로 조회
    Optional<GoodsReviewDto> findByOrder_GonoAndDelYn(Long gono, String delYn);

}
