package com.project.app.goods.service;

import java.util.Map;

import com.project.app.auth.dto.MemberDto;
import com.project.app.goods.dto.GoodsDto;

public interface GoodsService {

	Map<String, Object> findAll(int page, int size, String category, String search);

	void save(GoodsDto gdto);

	void delete(Long gno, MemberDto member);

	GoodsDto findById(Long gno);

}
