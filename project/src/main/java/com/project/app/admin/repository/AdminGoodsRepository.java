package com.project.app.admin.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.goods.dto.GoodsDto;

public interface AdminGoodsRepository extends JpaRepository<GoodsDto, Long> {
	Long countByStatus(String status);
	
	String WhereCondition = """
			WHERE (:status IS NULL OR :status = '' OR g.status = :status)
			  AND (:isBanner IS NULL OR :isBanner = '' OR g.is_banner = :isBanner)
	          AND (:stockStatus = 'low' AND g.stock_cnt <= 5 OR :stockStatus = 'out' AND g.stock_cnt = 0 OR :stockStatus IS NULL OR :stockStatus = '')
	          AND (:startDate IS NULL OR :startDate = '' OR g.crdt >= TO_TIMESTAMP(:startDate || ' 00:00:00', 'YYYY-MM-DD HH24:MI:SS'))
			  AND (:endDate IS NULL OR :endDate = '' OR g.crdt <= TO_TIMESTAMP(:endDate || ' 23:59:59', 'YYYY-MM-DD HH24:MI:SS'))
	          AND (g.price >= :minPrice)
			  AND (:maxPrice = 0 OR g.price <= :maxPrice)
	          AND (
	            :search IS NULL OR :search = '' OR
	            (:category = 'gname' AND g.gname LIKE '%' || :search || '%') OR
	            (:category = 'idolName' AND ip.name LIKE '%' || :search || '%') OR
	            (:category = 'sellerName' AND m.nickname LIKE '%' || :search || '%') OR
	            (:category IS NULL AND (g.gname LIKE '%' || :search || '%' OR m.nickname LIKE '%' || :search || '%'))
	          )
			""";
	
	@Query(value = """
	        SELECT 
	            g.gno AS "gno",
	            g.gname AS "gname",
	            m.nickname AS "sellerName",
	            g.price AS "price",
	            g.stock_cnt AS "stockCnt",
	            g.status AS "status",
	            g.is_banner AS "isBanner",
	            g.banner_sort AS "bannerSort",
	            ip.name AS "idolName",
	            TO_CHAR(g.crdt, 'YYYY-MM-DD') AS "orderDate",
	            COALESCE((SELECT ROUND(AVG(r.rating), 1) FROM goods_review r WHERE r.gno = g.gno AND r.del_yn = 'n'), 0) AS "avgRating",
	            (SELECT COUNT(*) FROM goods_review r WHERE r.gno = g.gno) AS "reviewCnt",						--총 리뷰수
	            COALESCE((SELECT COUNT(rl.grlno) 
	                      FROM goods_review_like rl 
	                      JOIN goods_review r ON rl.grno = r.grno 
	                      WHERE r.gno = g.gno AND r.del_yn = 'n'), 0) AS "helpfulCnt"
	        FROM goods g
	        JOIN member m ON g.id = m.id
	        LEFT JOIN idol_profile ip ON g.profileid = ip.profileid
	        """ + WhereCondition + """
	        ORDER BY 
			    CASE WHEN :sortDir = 'price_desc' THEN "price" END DESC,
			    CASE WHEN :sortDir = 'price_asc' THEN "price" END ASC,
	            CASE WHEN :sortDir = 'rating_desc' THEN "avgRating" END DESC,
			    CASE WHEN :sortDir = 'rating_asc' THEN "avgRating" END ASC,
			    CASE WHEN :sortDir = 'helpful_desc' THEN "helpfulCnt" END DESC,
			    CASE WHEN :sortDir = 'helpful_asc' THEN "helpfulCnt" END ASC,
			    CASE WHEN :sortDir = 'crdt_asc' THEN "orderDate" END ASC,
	            g.gno DESC
	        """, 
	        countQuery = "SELECT COUNT(*) FROM goods g JOIN member m ON g.id = m.id LEFT JOIN idol_profile ip ON g.profileid = ip.profileid "+WhereCondition,
	        nativeQuery = true)
	    Page<Map<String, Object>> findAdminGoodsMap(
	            @Param("search") String search,
	            @Param("category") String category,
	            @Param("status") String status,
	            @Param("stockStatus") String stockStatus,
	            @Param("isBanner") String isBanner,
	            @Param("minPrice") Long minPrice,
	            @Param("maxPrice") Long maxPrice,
	            @Param("startDate") String startDate,
	            @Param("endDate") String endDate,
	            @Param("sortDir") String sortDir,
	            Pageable pageable
	    );
}
