package com.project.app.goodsorders.dto;

import java.sql.Timestamp;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goodsSettlement.dto.GoodsSettlementDto;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "goods_orders")		//주문정보
public class GoodsOrdersDto {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gono;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private MemberDto member;  // 구매자
    
    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "idol_no")
    //private IdolDto idol; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gno")
    private GoodsDto goods;

    @Column(name = "total_price")
    private Long totalPrice; // 총구매가격 

    @Column(name = "cnt")
    private Long cnt; // 주문수량 

    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId; // 예: ORD-20240522-001 (사용자 노출용 고유번호)

    @Column(name = "tid", length = 50)
    private String tid; // 카카오페이 결제 고유 번호

    @Column(name = "payment_method", length = 20)
    private String paymentMethod; // KAKAO_PAY, CARD, CASH 등

    @Column(name = "status")
    private String status; // READY(대기), PAID(완료), CANCEL(취소), FAILED(실패)
    
    @Column(name = "tracking_no", length = 50)
    private String trackingNo; // 운송장 번호 (실제 배송 추적용)
    
    @Column(name = "cancel_reason")
    private String cancelReason; // 주문 취소 사유 (재고 부족 등)
    
    @ColumnDefault("'n'") // n: 정상, y: 삭제됨
    @Column(name="del_yn", length = 1)
    private String delYn = "n"; // 삭제여부
    
    @CreationTimestamp
    @Column(name = "crdt", updatable = false)
    private Timestamp crdt;		//등록일

    @UpdateTimestamp
    @Column(name = "updt")
    private Timestamp updt;		//수정일
    
    //@Embedded
	//private BaseEntity base;	//등록날짜, 등록자, 수정자 등
    
    // --- 정산 ----
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settle_id")
    @JsonIgnore
    private GoodsSettlementDto settlement;  // 정산

    @Column(name = "settle_yn", length = 1)
    @ColumnDefault("'n'")
    private String settleYn = "n";  // 정산 여부
    
	// --- 배송 정보 추가 ---
    @Column(name = "receiver_name", length = 50)
    private String receiverName; // 받는 사람 성함

    @Column(name = "receiver_phone", length = 20)
    private String receiverPhone; // 받는 사람 연락처

    @Column(name = "address")
    private String address; // 기본 배송받을 주소 (도로명 주소)

    @Column(name = "detail_address")
    private String detailAddress; // 배송받을 상세 주소

    @Column(name = "order_request")
    private String orderRequest; // 배송 요청사항 (예: 문 앞에 놓아주세요)
    
    @Column(name = "deliv_status", length = 20)
    @ColumnDefault("'배송대기'")
	private String delivStatus; // 배송상태(0: 배송대기, 1: 배송준비중, 2: 배송중, 3: 배송완료, 4: 구매확정)
    
    //@Column(name = "confirm_status", length = 20)
    //private String confirmStatus;	// 구매확정
    
	//@Column(name = "deliv_date")
	//private Timestamp delivDate; // 배송일
    
    //깔끔하게 삭제 후 등록으로 진행할 경우 필요없음
    //--- 반드시 기록해야 하는 것(판매자가 수정할 수 있음 - 반품에 안 들어가지만 구매당시 기록 조회용)
    @Column(name="gdeliv_price")
    private Long gdelPrice;  // 배송료 내역(판매자 - 배송료가 변경되어 수정가능)
    
    @Column(name="gdeliv_type")
    private String gdelType; // 택배사 내역(판매자 - 택배사가 변경되어 수정가능)
    
    @Lob
    @Column(name="gdeliv_addr")
    private String gdelivAddr; // 배송시작주소 내역(판매자 - 주소가 변경되어 수정가능)
    
    @Lob
    @Column(name="gdeliv_addr_return")
    private String gdelivAddrReturn; // 배송반품주소 내역(판매자 - 주소가 변경되어 수정가능)
    
    @Lob
    @Column(name="gdeliv_addr_return_detail")
    private String gdelivAddrReturnDetail; // 배송반품상세주소 내역(판매자 - 주소가 변경되어 수정가능)
    
    @Column(name = "delivery_complete_date")
    private Timestamp deliveryCompleteDate; // 배송 완료 일시

    @Column(name = "confirm_date")
    private Timestamp confirmDate; // 구매 확정 일시 (이 날짜 기준으로 정산 가능 상태 전환)
    
    @Column(name = "cancel_date")
    private Timestamp cancelDate; // 결제 취소 일시
    
    //계좌번호 같은건 생략
    
}
