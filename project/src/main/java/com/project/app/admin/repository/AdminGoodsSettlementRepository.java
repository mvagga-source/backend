package com.project.app.admin.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.goods.dto.GoodsDto;
import com.project.app.goodsSettlement.dto.GoodsSettlementDto;

public interface AdminGoodsSettlementRepository extends JpaRepository<GoodsSettlementDto, Long> {
	/*Long countByStatus(String status);
	
	String WHERE = """
	        WHERE (:status IS NULL OR :status = '' OR gs.status = :status)
	          AND (:startDate IS NULL OR :startDate = '' OR gs.crdt >= TO_TIMESTAMP(:startDate || ' 00:00:00','YYYY-MM-DD HH24:MI:SS'))
	          AND (:endDate IS NULL OR :endDate = '' OR gs.crdt <= TO_TIMESTAMP(:endDate || ' 23:59:59','YYYY-MM-DD HH24:MI:SS'))
	          AND (:minAmount IS NULL OR gs.settle_amount >= :minAmount)
	          AND (:maxAmount = 0 OR gs.settle_amount <= :maxAmount)
	          AND (:sellerName IS NULL OR :sellerName = '' OR m.nickname LIKE '%' || :sellerName || '%')
	    """;

	    @Query(value = """
	        SELECT
	            gs.settle_id AS "settleId",
	            m.nickname AS "sellerName",
	            gs.total_amount AS "totalAmount",
	            gs.fee_amount AS "feeAmount",
	            gs.settle_amount AS "settleAmount",
	            gs.status AS "status",
	            TO_CHAR(gs.crdt, 'YYYY-MM-DD') AS "crdt"
	        FROM goods_settlement gs
	        JOIN member m ON gs.seller_id = m.id
	        """ + WHERE + """
	        ORDER BY
	            CASE WHEN :sortDir = 'amount_desc' THEN gs.settle_amount END DESC,
	            CASE WHEN :sortDir = 'amount_asc' THEN gs.settle_amount END ASC,
	            CASE WHEN :sortDir = 'crdt_asc' THEN gs.crdt END ASC,
	            gs.settle_id DESC
	        """,
	        countQuery = """
	        SELECT COUNT(*)
	        FROM goods_settlement gs
	        JOIN member m ON gs.seller_id = m.id
	        """ + WHERE,
	        nativeQuery = true
	    )
	    Page<Map<String, Object>> findSettlementList(
	    	@Param("sellerName") String sellerName,
	        @Param("status") String status,
	        @Param("startDate") String startDate,
	        @Param("endDate") String endDate,
	        @Param("minAmount") Long minAmount,
	        @Param("maxAmount") Long maxAmount,
	        @Param("sortDir") String sortDir,
	        Pageable pageable
	    );*/
	    
	 	// 정산 상태별 카운트 (통계용)
	    Long countByStatus(String status);
	    
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
	        CASE 
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
		    END)) AS "settleAmount",
			gr.return_reason AS "returnReason",	--반품사유
	        o.status AS "orderStatus",
	        o.cnt AS "orderCnt",                 -- 원 주문 수량
	        (o.cnt - gr.return_cnt) AS "realCnt", -- 실 수량
	        o.deliv_status AS "delivStatus",
	        o.settle_yn AS "settleYn",			--정산여부
	        TO_CHAR(o.crdt, 'YYYY-MM-DD HH24:MI:SS') AS "orderDate",
			TO_CHAR(s.crdt, 'YYYY-MM-DD HH24:MI:SS') AS "settleDate"		-- 정산일
	        FROM goods_orders o
	        JOIN goods g ON o.gno = g.gno
	        JOIN member m ON o.id = m.id
	        JOIN member seller ON g.id = seller.id
	        LEFT OUTER JOIN goods_return gr on gr.gono = o.gono
	        LEFT OUTER JOIN goods_settlement s on s.settle_id = o.settle_id
	        """+WHERE_CONDITION,
	        countQuery = """
		        		SELECT COUNT(*) FROM goods_orders o
	                    JOIN goods g ON o.gno = g.gno
	                    JOIN member m ON o.id = m.id
	                    JOIN member seller ON g.id = seller.id
	                    LEFT OUTER JOIN goods_return gr on gr.gono = o.gono
				        LEFT OUTER JOIN goods_settlement s on s.settle_id = o.settle_id
						"""+WHERE_CONDITION,
	        nativeQuery = true
	    )
	    Page<Map<String, Object>> findSettlementList(
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
}
