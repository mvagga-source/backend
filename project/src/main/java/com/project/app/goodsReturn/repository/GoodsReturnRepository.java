package com.project.app.goodsReturn.repository;

import java.sql.Timestamp;

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
}
