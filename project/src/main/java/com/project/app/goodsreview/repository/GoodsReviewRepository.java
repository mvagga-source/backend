package com.project.app.goodsreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.goodsorders.dto.GoodsOrdersDto;
import com.project.app.goodsreview.dto.GoodsReviewDto;

public interface GoodsReviewRepository extends JpaRepository<GoodsReviewDto, Long> {

}
