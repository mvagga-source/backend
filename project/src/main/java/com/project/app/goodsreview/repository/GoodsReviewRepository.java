package com.project.app.goodsreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.goodsorders.dto.GoodsOrdersDto;

public interface GoodsReviewRepository extends JpaRepository<GoodsOrdersDto, Long> {

}
