package com.project.app.goodsReturn.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.goodsReturn.dto.GoodsReturnDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;

public interface GoodsReturnRepository extends JpaRepository<GoodsReturnDto, Long> {
	// 특정 회원의 반품 내역을 날짜 범위 내에서 페이징 조회 (삭제되지 않은 것만)
    @Query("SELECT r FROM GoodsReturnDto r " +
           "WHERE r.member.id = :memberId " +
           "AND r.delYn = 'n' " +
           "AND (:startDate IS NULL OR r.crdt >= :startDate) " +
           "AND (:endDate IS NULL OR r.crdt <= :endDate) " +
           "ORDER BY r.crdt DESC")
    Page<GoodsReturnDto> findMyReturnList(
            @Param("memberId") String memberId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            Pageable pageable);
    
	//판매자 반품/교환 리스트 조회
  	@Query(value="""
  		    SELECT 
	  		    gr.rno as "rno",
		        gr.return_status as "returnStatus",
		        gr.gono as "gono",
		        gr.refund_price as "refundPrice",
		        gr.return_cnt as "returnCnt",
		        gr.return_type as "returnType",
		        gr.return_reason as "returnReason",
		        TO_CHAR(gr.return_reason_detail) as "returnReasonDetail",
		        gr.id as "memberId",
		        gr.crdt as "crdt",
		        gr.gdeliv_price as "gdelivPrice",
		        gr.gdeliv_type as "gdelivType",
		        gr.pickup_addr as "pickupAddr",
		        TO_CHAR(gr.pickup_addr_detail) as "pickupAddrDetail",
		        gr.pickup_name as "pickupName",
		        gr.pickup_phone as "pickupPhone",
		        gr.order_request as "orderRequest",
		        TO_CHAR(gr.gdeliv_addr) as "gdelivAddr",
		        TO_CHAR(gr.gdeliv_addr_return) as "gdelivAddrReturn",
		        TO_CHAR(gr.gdeliv_addr_return_detail) as "gdelivAddrReturnDetail",
	  		    go.order_id as "orderId", g.gname as "gname", m.nickname as "buyerName"
  		    FROM goods_return gr
  		    JOIN goods_orders go ON gr.gono = go.gono
  		    JOIN goods g ON go.gno = g.gno
  		    JOIN member m ON go.id = m.id
  		    WHERE g.id = :memberId
  		      AND gr.del_yn = 'n'
  		      AND (:startDate IS NULL OR gr.crdt >= :startDate)
    		  AND (:endDate IS NULL OR gr.crdt <= :endDate)
  		    """,
  		    countQuery = """
  		    SELECT count(*) FROM goods_return gr
  		    JOIN goods_orders go ON gr.gono = go.gono
  		    JOIN goods g ON go.gno = g.gno
  		    WHERE g.id = :memberId
  		      AND gr.del_yn = 'n'
  		      AND (:startDate IS NULL OR gr.crdt >= :startDate)
    		  AND (:endDate IS NULL OR gr.crdt <= :endDate)
  		    """,
  		    nativeQuery = true)
  		Page<Map<String, Object>> findSellerReturnList(
  		        @Param("memberId") String memberId, 
  		        @Param("startDate") Timestamp startDate, 
  		        @Param("endDate") Timestamp endDate,            
  		        Pageable pageable);
    
    @Query("SELECT SUM(r.returnCnt) FROM GoodsReturnDto r WHERE r.order.gono = :gono AND r.delYn = 'n' AND r.returnStatus IN ('접수', '회수중', '완료')") // 반품 거부, 취소 상태는 제외
    Long sumReturnCntByGono(@Param("gono") Long gono);
    
    Optional<GoodsReturnDto> findByRnoAndDelYn(Long rno, String delYn);
    
    //리뷰에서 반품 수량이 있는지 검사
    List<GoodsReturnDto> findAllByOrder_GonoAndDelYn(Long gono, String delYn);
}
