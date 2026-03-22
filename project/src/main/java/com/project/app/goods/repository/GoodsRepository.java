package com.project.app.goods.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.goods.dto.GoodsDto;

public interface GoodsRepository extends JpaRepository<GoodsDto, Long> {

	public Page<GoodsDto> findByGcontentContainingAndDelYn(String search, String string, Pageable pageable);
	
	public Page<GoodsDto> findByDelYn(String string, Pageable pageable);

}
