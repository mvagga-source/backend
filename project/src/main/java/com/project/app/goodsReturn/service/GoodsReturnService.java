package com.project.app.goodsReturn.service;

import java.util.Map;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.AjaxResponse;
import com.project.app.common.exception.BaCdException;
import com.project.app.goodsReturn.dto.GoodsReturnDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;

public interface GoodsReturnService {

	public GoodsReturnDto requestReturn(GoodsReturnDto returnRequest, MemberDto memberDto) throws BaCdException;

	public Map<String, Object> findByGono(Long gono, MemberDto memberDto) throws BaCdException;
	
	public Map<String, Object> list(Map<String, Object> param) throws BaCdException;

	public GoodsReturnDto delete(Long rno) throws BaCdException;

	public Map<String, Object> findSellerReturnList(Map<String, Object> param) throws BaCdException;

	public Map<String, Object>  findById(Long rno, MemberDto idCheck) throws BaCdException;

	public GoodsReturnDto updateStatus(Long rno, String nextStatus, Long gdelPrice, String gdelType,
			String returnReasonDetail, MemberDto member) throws BaCdException;

	public Map<String, Object> view(Long rno, MemberDto idCheck) throws BaCdException;

	GoodsReturnDto update(GoodsReturnDto dto, MemberDto memberDto) throws BaCdException;
	
}
