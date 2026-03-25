package com.project.app.goodsorders.service;

import java.util.Map;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.AjaxResponse;
import com.project.app.common.exception.BaCdException;
import com.project.app.goodsorders.dto.GoodsOrdersDto;

public interface GoodsOrdersService {

	public GoodsOrdersDto createOrder(GoodsOrdersDto dto) throws BaCdException;

	public GoodsOrdersDto findByGono(Long gono) throws BaCdException;

	public void updateStatus(String orderId, String tid, String status) throws BaCdException;

	public Map<String, Object> findMyOrders(MemberDto member, int page, int size) throws BaCdException;

	GoodsOrdersDto findByGono(GoodsOrdersDto dto) throws BaCdException;

	public Map<String, Object> readyPayment(GoodsOrdersDto orderRequest, MemberDto memberDto) throws BaCdException;

	public Map<String, Object> approvePayment(String pgToken, String tid, MemberDto memberDto) throws BaCdException;
	
	public void failOrCancelPayment(String tid, String status) throws BaCdException;

}
