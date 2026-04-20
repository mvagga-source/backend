package com.project.app.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.goodsReturn.dto.GoodsReturnDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AdminGoodsReturnRepository extends JpaRepository<GoodsReturnDto, Long> {
	//조회조건은 querydsl 이용하는게 나은
	
	//건수 쿼리에도 동일한 조건 사용
	final String WHERE_CONDITION = """
		    WHERE gr.del_yn = 'n'
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
			AND (:returnStatus IS NULL OR :returnStatus = '' OR gr.return_status = :returnStatus)
			AND (:returnType IS NULL OR :returnType = '' OR gr.return_type = :returnType)
			AND (:returnReason IS NULL OR :returnReason = '' OR gr.return_reason = :returnReason)
			AND (:delivStatus IS NULL OR :delivStatus = '' OR gr.deliv_status = :delivStatus)
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
        o.total_price AS "totalPrice",
        gr.rno AS "rno",							--pk
		gr.return_reason AS "returnReason",	--반품사유
		TO_CHAR(gr.return_reason_detail) AS "returnReasonDetail",	--반품사유상세
		TO_CHAR(gr.return_sale_reason_detail) AS "returnSaleReasonDetail",	--판매자반품거부사유
		gr.gdeliv_price AS "gdelivPrice",	--반품배송비
		gr.return_cnt AS "returnCnt",	--반품수량
		gr.return_type AS "returnType",	--반품구분
		gr.return_status AS "returnStatus",	--반품상태
        o.status AS "orderStatus",			--주문상태
        o.cnt AS "orderCnt",                 -- 원 주문 수량
        (o.cnt - gr.return_cnt) AS "realCnt", -- 반품가능한 수량
        gr.deliv_status AS "delivStatus",	--반품배송상태
        o.settle_yn AS "settleYn",			--정산여부
        TO_CHAR(gr.crdt, 'YYYY-MM-DD HH24:MI:SS') AS "returnDate",
        TO_CHAR(gr.updt, 'YYYY-MM-DD HH24:MI:SS') AS "returnUpDate",
        TO_CHAR(gr.complete_date, 'YYYY-MM-DD HH24:MI:SS') AS "completeDate"
        FROM goods_orders o
        JOIN goods g ON o.gno = g.gno
        JOIN member m ON o.id = m.id
        JOIN member seller ON g.id = seller.id
        JOIN goods_return gr ON o.gono = gr.gono
        """+WHERE_CONDITION,
        countQuery = """
	        		SELECT COUNT(*) FROM goods_orders o
                    JOIN goods g ON o.gno = g.gno
                    JOIN member m ON o.id = m.id
                    JOIN member seller ON g.id = seller.id
			        JOIN goods_return gr ON o.gono = gr.gono
					"""+WHERE_CONDITION,
        nativeQuery = true
    )
    Page<Map<String, Object>> findAdminOrdersMap(
        @Param("search") String search,
        @Param("category") String category,
        @Param("status") String status,
        @Param("delivStatus") String delivStatus,
        @Param("returnStatus") String returnStatus,
		@Param("returnType") String returnType,
		@Param("returnReason") String returnReason,
        @Param("settleYn") String settleYn,
        @Param("minPrice") int minPrice,
        @Param("maxPrice") int maxPrice,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        Pageable pageable
    );
    
	// 현재 수정 중인 반품건(rno)을 제외하고, 해당 주문(gono)에 걸린 모든 반품 수량의 합계를 구함
    @Query("SELECT COALESCE(SUM(gr.returnCnt), 0) FROM GoodsReturnDto gr WHERE gr.delYn='n' AND gr.order.gono = :gono AND gr.rno != :rno AND gr.returnStatus NOT IN ('취소', '거부')")
    Long sumReturnCntByGonoExceptRno(@Param("gono") Long gono, @Param("rno") Long rno);
    
    Optional<GoodsReturnDto> findByRnoAndDelYn(Long rno, String delYn);
    
    //Optional<GoodsReturnDto> findByOrderAndDelYn(GoodsOrdersDto order, String delYn);
    
    List<GoodsReturnDto> findAllByOrderAndDelYn(GoodsOrdersDto order, String delYn);
}