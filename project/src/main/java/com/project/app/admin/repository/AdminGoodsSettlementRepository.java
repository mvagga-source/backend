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
	 	// 정산 상태별 카운트 (통계용)
	    Long countByStatus(String status);
	    
	    final String WHERE_CONDITION = """
			    WHERE 1=1 --o.del_yn = 'n'
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
				AND (:minAmount IS NULL OR s.total_amount >= :minAmount)
				AND (:maxAmount IS NULL OR :maxAmount = 0 OR s.total_amount <= :maxAmount)
				AND ((:startDate IS NULL OR :startDate = '') OR s.crdt >= TO_DATE(:startDate, 'YYYY-MM-DD'))
				AND ((:endDate IS NULL OR :endDate = '') OR s.crdt <= TO_DATE(:endDate, 'YYYY-MM-DD') + 1)
			""";

		//택배비용은 송장번호 몇개 상자인지에 따라 다름 (대량구매시 배송비가 더 나올 수 있음 - 소규모라 그런것까지 고려X)
	    @Query(value ="""
	    	SELECT
	        s.settle_id AS "settleId",
		    seller.nickname AS "sellerName",
		    s.total_amount AS "totalAmount",
		    s.refund_amount AS "refundAmount",
		    s.pg_fee AS "pgFee",
		    s.platform_fee AS "platformFee",
		    s.tax_amount AS "taxAmount",
		    s.settle_amount AS "settleAmount",
		    s.status AS "status",
		    TO_CHAR(s.crdt, 'YYYY-MM-DD HH24:MI:SS') AS "settleDate",
		    (SELECT COUNT(*) FROM goods_orders o WHERE o.settle_id = s.settle_id) AS "orderCount"
	        FROM goods_settlement s
			LEFT JOIN goods_orders o ON o.settle_id = s.settle_id
	        LEFT JOIN goods g ON o.gno = g.gno
	        LEFT JOIN member m ON o.id = m.id
	        LEFT JOIN member seller ON g.id = seller.id
	        """+WHERE_CONDITION+"""
	        		GROUP BY 
				    s.settle_id,
				    seller.nickname,
				    s.total_amount,
				    s.refund_amount,
				    s.pg_fee,
				    s.platform_fee,
				    s.tax_amount,
				    s.settle_amount,
				    s.status,
				    s.crdt
	        		""",
	        countQuery = """
		        		SELECT COUNT(DISTINCT s.settle_id)
						FROM goods_settlement s
						LEFT JOIN goods_orders o ON o.settle_id = s.settle_id
				        LEFT JOIN goods g ON o.gno = g.gno
				        LEFT JOIN member m ON o.id = m.id
				        LEFT JOIN member seller ON g.id = seller.id
						"""+WHERE_CONDITION+"""
							GROUP BY 
						    s.settle_id,
						    seller.nickname,
						    s.total_amount,
						    s.refund_amount,
						    s.pg_fee,
						    s.platform_fee,
						    s.tax_amount,
						    s.settle_amount,
						    s.status,
						    s.crdt
						""",
	        nativeQuery = true
	    )
	    Page<Map<String, Object>> findSettlementList(
	        @Param("search") String search,
	        @Param("category") String category,
	        @Param("status") String status,
	        @Param("settleYn") String settleYn,
	        @Param("minAmount") Long minAmount,
	        @Param("maxAmount") Long maxAmount,
	        @Param("startDate") String startDate,
	        @Param("endDate") String endDate,
	        Pageable pageable
	    );
}
