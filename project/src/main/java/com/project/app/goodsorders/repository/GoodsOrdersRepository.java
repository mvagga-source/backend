package com.project.app.goodsorders.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.goodsorders.dto.GoodsOrdersDto;

public interface GoodsOrdersRepository extends JpaRepository<GoodsOrdersDto, Long> {
	
	public Optional<GoodsOrdersDto> findByOrderId(String orderId);

	public Page<GoodsOrdersDto> findByMemberIdAndDelYn(String id, String string, Pageable pageable);
	
	public Optional<GoodsOrdersDto> findByTid(String tid);
	
}
