package com.project.app.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.goodsorders.dto.GoodsOrdersDto;

import java.util.List;
import java.util.Map;

public interface AdminGoodsOrdersRepository extends JpaRepository<GoodsOrdersDto, Long> {
	//조회조건은 querydsl 이용하는게 나은
	
	//건수 쿼리에도 동일한 조건 사용
	final String WHERE_CONDITION = """
		    WHERE o.del_yn = 'n'
		    AND (
			    :search IS NULL OR :search = '' OR
			    (
			        (:category IS NULL OR :category = '') AND
				        (o.order_id LIKE '%' || :search || '%' OR
				         g.gname LIKE '%' || :search || '%' OR
				         m.nickname LIKE '%' || :search || '%' OR
				         seller.nickname LIKE '%' || :search || '%')
				    ) OR
			    (:category = 'orderId' AND o.order_id LIKE '%' || :search || '%') OR
			    (:category = 'gname' AND g.gname LIKE '%' || :search || '%') OR
			    (:category = 'buyerName' AND m.nickname LIKE '%' || :search || '%') OR
			    (:category = 'sellerName' AND seller.nickname LIKE '%' || :search || '%')
			)
			AND (:status IS NULL OR :status = '' OR o.status = :status)
			AND (:delivStatus IS NULL OR :delivStatus = '' OR o.deliv_status = :delivStatus)
			AND (:settleYn IS NULL OR :settleYn = '' OR o.settle_yn = :settleYn)
			AND (:minPrice IS NULL OR o.total_price >= :minPrice)
			AND (:maxPrice IS NULL OR :maxPrice = 0 OR o.total_price <= :maxPrice)
			AND ((:startDate IS NULL OR :startDate = '') OR o.crdt >= TO_DATE(:startDate, 'YYYY-MM-DD'))
			AND ((:endDate IS NULL OR :endDate = '') OR o.crdt <= TO_DATE(:endDate, 'YYYY-MM-DD') + 1)
		""";

	//택배비용은 송장번호 몇개 상자인지에 따라 다름 (대량구매시 배송비가 더 나올 수 있음 - 소규모라 그런것까지 고려X)
    @Query(value ="""
    	SELECT
        o.gono AS "gono",
        o.order_id AS "orderId",
        g.gname AS "gname",
        g.status AS "goodsStatus",
        seller.nickname AS "sellerName",
        m.nickname AS "buyerName",
        o.total_price AS "totalPrice",							--결제금액
        /*CASE 
	        WHEN gr.rno IS NULL THEN o.total_price  -- 반품 없으면 원래 금액
	        WHEN gr.return_status != '완료' THEN o.total_price -- 반품 진행 중이면 일단 보류
	        ELSE (o.total_price - COALESCE(gr.refund_price, 0)) -- 반품 완료 시 환불액 차감
	    END AS "finalTotalPrice",	--최종 결제금액
        -- 수수료 및 정산금액 재계산 (최종 금액 기준)
        ROUND((o.total_price - COALESCE(gr.refund_price, 0)) * 0.033) AS "fee",
        -- 최종 정산 예정액 = (실매출 * 0.967) + 배송비 페널티
	    -- 여기서 마이너스가 나올 수 있습니다.
	    ROUND((CASE 
	        WHEN gr.rno IS NULL THEN o.total_price 
	        ELSE (o.total_price - COALESCE(gr.refund_price, 0)) 
	    END * 0.967) + 
	    (CASE 
	        WHEN gr.return_status = '완료' AND gr.return_reason IN ('파손', '오배송') 
	        THEN -(COALESCE(gr.gdeliv_price, 0) * 2) 
	        ELSE 0 
	    END)) AS "settleAmount",*/
		gr.return_reason AS "returnReason",	--반품사유
        o.status AS "orderStatus",
        o.cnt AS "orderCnt",                 -- 원 주문 수량
        gr.return_cnt AS "returnCnt",         -- 반품 수량
        (o.cnt - gr.return_cnt) AS "realCnt", -- 실 수량
        o.deliv_status AS "delivStatus",
        o.settle_yn AS "settleYn",			--정산여부
        TO_CHAR(o.crdt, 'YYYY-MM-DD HH24:MI:SS') AS "orderDate",
		/*TO_CHAR(s.crdt, 'YYYY-MM-DD HH24:MI:SS') AS "settleDate",*/		-- 정산일
		(SELECT ROUND(r.rating, 1) FROM goods_review r WHERE r.gono = o.gono) AS "rating",			-- 별점
        --(SELECT ROUND(AVG(r.rating), 1) FROM goods_review r WHERE r.gno = g.gno) AS "avgRating",		--평균별점
        (SELECT COUNT(*) FROM goods_review r WHERE r.gno = g.gno) AS "reviewCnt"						--총 리뷰수
        FROM goods_orders o
        JOIN goods g ON o.gno = g.gno
        JOIN member m ON o.id = m.id
        JOIN member seller ON g.id = seller.id
        LEFT OUTER JOIN (
	        SELECT 
	            gono, 
	            SUM(return_cnt) as return_cnt, 
	            SUM(refund_price) as refund_price,
	            MAX(return_reason) as return_reason  -- 대표 사유 하나만 가져옴
	        FROM goods_return 
	        WHERE del_yn = 'n'
	        GROUP BY gono
	    ) gr ON o.gono = gr.gono
        --LEFT OUTER JOIN goods_settlement s on s.settle_id = o.settle_id
        """+WHERE_CONDITION,
        countQuery = """
	        		SELECT COUNT(*) FROM goods_orders o
                    JOIN goods g ON o.gno = g.gno
                    JOIN member m ON o.id = m.id
                    JOIN member seller ON g.id = seller.id
			        LEFT OUTER JOIN (
				        SELECT 
				            gono, 
				            SUM(return_cnt) as totalReturnCnt, 
				            SUM(refund_price) as totalRefundPrice,
				            MAX(return_reason) as returnReason  -- 대표 사유 하나만 가져옴
				        FROM goods_return 
				        WHERE del_yn = 'n'
				        GROUP BY gono
				    ) gr ON o.gono = gr.gono
			        --LEFT OUTER JOIN goods_settlement s on s.settle_id = o.settle_id
					"""+WHERE_CONDITION,
        nativeQuery = true
    )
    Page<Map<String, Object>> findAdminOrdersMap(
        @Param("search") String search,
        @Param("category") String category,
        @Param("status") String status,
        @Param("delivStatus") String delivStatus,
        @Param("settleYn") String settleYn,
        @Param("minPrice") int minPrice,
        @Param("maxPrice") int maxPrice,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        Pageable pageable
    );
    
	// 오늘 매출
    @Query(value = """
	    SELECT COALESCE(SUM(total_price), 0)
	    FROM goods_orders
	    WHERE TRUNC(crdt) = TRUNC(SYSDATE)
	    AND status = 'PAID'
	    """, nativeQuery = true)
	Long sumTodaySales();

    // 배송대기
    Long countByDelivStatus(String status);

    // 취소
    Long countByStatus(String status);

    // 주간 매출
    @Query(value = """
	    SELECT COALESCE(SUM(total_price), 0)
	    FROM goods_orders
	    WHERE status = 'PAID'
	      AND crdt >= TRUNC(SYSDATE) - 6
	    GROUP BY TRUNC(crdt)
	    ORDER BY TRUNC(crdt) ASC
	    """, nativeQuery = true)
	List<Long> weeklySales();

    // 아이돌별 판매 비중
    @Query("""
        SELECT i.name, SUM(o.totalPrice)
        FROM GoodsOrdersDto o
        JOIN o.goods g
        JOIN g.idol i
        GROUP BY i.name
    """)
    List<Object[]> idolSalesRatio();

	long countByGoods_Gno(Long gno);
}