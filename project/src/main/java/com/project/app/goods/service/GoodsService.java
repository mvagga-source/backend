package com.project.app.goods.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.dto.GoodsDto;

public interface GoodsService {

	public Map<String, Object> findAll(int page, int size, int minPrice, int maxPrice, String category, String search, String sortDir, String view) throws BaCdException;

	public GoodsDto save(GoodsDto gdto, MultipartFile gimgFile) throws BaCdException;
	
	public GoodsDto update(GoodsDto gdto, MultipartFile gimgFile, MemberDto member) throws BaCdException;

	public void delete(Long gno, MemberDto member) throws BaCdException;

	public GoodsDto findById(Long gno) throws BaCdException;

	public Map<String, Object> findBannerList(int limit) throws BaCdException;

}
