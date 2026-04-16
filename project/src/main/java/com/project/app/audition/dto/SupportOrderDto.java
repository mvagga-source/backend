package com.project.app.audition.dto;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "support_order")
public class SupportOrderDto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
    private Long orderId;; 
    
    @Column(name = "partner_order_id", unique = true)
    private String partnerOrderId; // 우리가 직접 생성한 주문번호 (예: ORD2026...)

    @Column(nullable = false)
    private String tid; // 카카오페이에서 발급받은 결제 고유 번호 (승인 시 필수)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_id")
    private SupportProjectDto supportProject; // 어떤 프로젝트에 후원하는지

    @Column(nullable = false)
    private String nickname; // 후원자 닉네임

    @Column(nullable = false)
    private Long amount; // 후원 금액 (카카오페이에 보낸 금액과 일치해야 함)

    @Column(nullable = false)
    private String status; // 결제 상태: READY(준비), PAID(완료), CANCEL(취소), FAIL(실패)

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Timestamp createdAt;
}
