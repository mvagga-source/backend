package com.project.app.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.goods.dto.GoodsDto;

public interface AdminGoodsRepository extends JpaRepository<GoodsDto, Long> {
	Long countByStatus(String status);
}
