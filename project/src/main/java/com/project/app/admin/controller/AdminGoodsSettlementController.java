package com.project.app.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.admin.service.AdminGoodsService;
import com.project.app.admin.service.AdminGoodsSettlementService;
import com.project.app.auth.dto.MemberDto;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/settlement")
@RequiredArgsConstructor
public class AdminGoodsSettlementController {
	
	private final AdminGoodsService adminGoodsService;
	
	private final AdminGoodsSettlementService adminGoodsSettlementService;
	
	private final HttpSession session;
	
	@GetMapping("/ajaxList")
	@ResponseBody
	public Map<String, Object> ajaxList(@RequestParam(name="page", defaultValue="1") int page,
	                             @RequestParam(name="perPage", defaultValue="10") int perPage,
	                             @RequestParam(name="category", required=false) String category,
	                             @RequestParam(name="search", defaultValue="") String search,
	                             @RequestParam(name="sellerName", required=false) String sellerName,
	                             @RequestParam(name="status", required=false) String status,
	                             @RequestParam(name="stockStatus", required=false) String stockStatus,
	                             @RequestParam(name="delivStatus", required=false, defaultValue="") String delivStatus,
	                             @RequestParam(name="minAmount", defaultValue="0") Long minAmount,
	                             @RequestParam(name="maxAmount", defaultValue="0") Long maxAmount,
	                             @RequestParam(name="startDate", required=false) String startDate,
	                             @RequestParam(name="endDate", required=false) String endDate,
	                             @RequestParam(name="minPrice", defaultValue="0") int minPrice,
	                             @RequestParam(name="maxPrice", defaultValue="0") int maxPrice,
	                             @RequestParam(name="settleYn", defaultValue="n") String settleYn,
	                             @RequestParam(name="sortBy", defaultValue="DESC") String sortBy, 
	                             @RequestParam(name="sortDir", defaultValue="settleId_desc") String sortDir) {
	    Common.adminIdCheck(session);
	    return adminGoodsSettlementService.list(page, perPage, minPrice, maxPrice, settleYn, delivStatus,
                category, status, search, sortDir, sortBy, startDate, endDate, minAmount, maxAmount);
	}
	
	@PostMapping("/ajaxModify")
	@ResponseBody
	public AjaxResponse ajaxModifyGoods(@RequestBody Map<String, Object> modifiedData) {
	    Common.adminIdCheck(session);
	    List<Map<String, Object>> updatedRows = (List<Map<String, Object>>) modifiedData.get("updatedRows");
	    
	    // AdminGoodsService에서 상품명, 가격, 재고, 상태값을 업데이트하는 로직 구현
	    return AjaxResponse.success(adminGoodsSettlementService.updateSettlementStatus(updatedRows));
	}
	
	@PostMapping("/settle")
	@ResponseBody
	public AjaxResponse settleOrders(@RequestBody List<Long> orderIds) {

	    // 관리자 체크
	    MemberDto admin = Common.adminIdCheck(session);

	    adminGoodsSettlementService.settleOrders(orderIds, admin);

	    return AjaxResponse.success("정산 완료");
	}
}