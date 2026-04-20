package com.project.app.goodsReturn.dto;

import java.sql.Timestamp;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goodsSettlement.dto.GoodsSettlementDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;

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
@Table(name = "goods_return")		//반품
public class GoodsReturnDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno; // 반품 고유 번호
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id") // 또는 name = "member_id" (DB 관례상 더 추천)
    private MemberDto member; // 반품 요청자 (회원)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gono")
    private GoodsOrdersDto order; // 연결된 주문 번호
    
    @Column(name = "return_type")
    @ColumnDefault("'반품'")
    private String returnType; // 반품, 교환 (두 가지 타입)

    @Column(name = "return_cnt")
    private Long returnCnt; // 반품 수량

    @Column(name = "return_reason")
    private String returnReason; 	// 반품 사유 (변심, 파손 등) -> 변심, 파손, 오배송, 지연

    @Lob
    @Column(name = "return_reason_detail")
    private String returnReasonDetail; 	// 구매자 상세 사유
    
    @Lob
    @Column(name = "return_sale_reason_detail")
    private String returnSaleReasonDetail;		//판매자 상세 사유

    @Column(name = "refund_price")
    private Long refundPrice; // 계산된 환불 예정 금액(price * qty) 배송비는 제외

    @Column(name = "return_status")
    @ColumnDefault("'접수'")
    private String returnStatus; // 접수, 회수중, 검수대기, 검수중, 완료, 거부, 취소

    @ColumnDefault("'n'") // n: 정상, y: 삭제됨
    @Column(name="del_yn", length = 1)
    private String delYn = "n"; // 삭제여부
    
    @CreationTimestamp
    private Timestamp crdt; // 반품 신청일

    @UpdateTimestamp
    private Timestamp updt; // 상태 변경일
    
    @Column(name = "complete_date")
    private Timestamp completeDate;	// 완료일
    
    @Column(name="pickup_addr")
    private String pickupAddr = ""; // 고객이 입력한 수거지 기본 주소

    @Lob
    @Column(name="pickup_addr_detail")
    private String pickupAddrDetail = ""; // 고객이 입력한 수거지 상세 주소
    
    @Column(name="pickup_name", length = 50)
    private String pickupName = ""; 	//수거지 성함(반품 보내는분)

    @Column(name="pickup_phone", length = 20)
    private String pickupPhone = ""; // 수거 관련 연락처 (주문자 번호와 다를 수 있음)
    
    @Column(name = "order_request")
    private String orderRequest; // 배송 요청사항 (예: 문 앞에 놔뒀습니다.)
    
	//--- 반드시 기록해야 하는 것(판매자가 수정할 수 있음)
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
    
    @Column(name = "deliv_status", length = 20)
    @ColumnDefault("'배송대기'")
	private String delivStatus; // 반품 배송상태(0: 배송대기, 1: 배송준비중, 2: 배송중, 3: 배송완료)
    
    //계좌번호같은건 생략
    
    //교환일 경우 상품을 다시 배송되므로 정산처리할 때 돈을 해당 주문내역에 포함하지 않음(반품처럼 반품 배송후 다시 받을 장소로 배송이 진행됨)
    //과정이 더 길어져서 생략
}
