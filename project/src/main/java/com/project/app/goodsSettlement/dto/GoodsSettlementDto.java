package com.project.app.goodsSettlement.dto;


import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.project.app.auth.dto.MemberDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "goods_settlement")  // 정산 테이블
public class GoodsSettlementDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settle_id")
    private Long settleId;  // 정산번호 (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private MemberDto seller;  // 판매자 (정산 대상)

    @Column(name = "total_amount")
    private Long totalAmount;    // 주문 총액 합계
    
    @Column(name = "fee_amount")
    private Long feeAmount;      // 수수료 합계
    
    @Column(name = "settle_amount")
    private Long settleAmount;   // 실제 지급 금액 (총액 - 수수료)

    @Column(length = 20)
    private String status;       // PENDING, COMPLETED, CANCELLED

    @CreationTimestamp
    @Column(name = "crdt", updatable = false)
    private Timestamp crdt;  // 정산 생성일

    @OneToMany(mappedBy = "settlement", fetch = FetchType.LAZY)
    private List<GoodsOrdersDto> orders;  // 포함된 주문들
}