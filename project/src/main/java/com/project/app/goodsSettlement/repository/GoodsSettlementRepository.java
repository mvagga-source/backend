package com.project.app.goodsSettlement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.auth.dto.MemberDto;
import com.project.app.goodsSettlement.dto.GoodsSettlementDto;

public interface GoodsSettlementRepository extends JpaRepository<GoodsSettlementDto, Long> {

    // 판매자별 정산 내역 조회
    List<GoodsSettlementDto> findBySellerOrderByCrdtDesc(MemberDto seller);

    // 상태별 정산 조회
    List<GoodsSettlementDto> findByStatus(String status);

}