package com.project.app.commissionPolicy.dto;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수수료 / 세금 정책 DTO
 * - 관리자에서 수정 가능
 * - 현재 활성 1건 기준 운영
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "commission_policy")			//수수료 / 세금 정책
public class CommissionPolicyDto {			//계속 쌓이는 구조(나중에 확장시 사용)

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)		//한행만 사용할때는 필요없지만 나중에 확장성 때문에 추가
    @Column(name = "cpyid")
    private Long cpyid;

    // PG 수수료(카카오페이 수수료) (예: 0.033 = 3.3%)
    @Column(name = "pg_fee_rate", nullable = false)
    private Double pgFeeRate;

    // 플랫폼 수수료(운영 수익) (예: 0.01 = 1%)
    @Column(name = "platform_fee_rate", nullable = false)
    private Double platformFeeRate;

    // 세금 (예: 0.1 = 10%)
    @Column(name = "tax_rate", nullable = false)
    private Double taxRate;

    // 활성 여부 (Y/N)
    //@Column(name = "is_active", length = 1)		//(당장은 1컬럼으로 사용하다가 나중에 확장시 사용, startDate, endDate도 있으면 좋음)
    //private String isActive;

    // 생성일
    @CreationTimestamp
    @Column(name = "crdt", updatable = false)
    private Timestamp crdt;

    // 수정일
    @UpdateTimestamp
    @Column(name = "updt")
    private Timestamp updt;
}
