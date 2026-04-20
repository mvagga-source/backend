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
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/goods")
@RequiredArgsConstructor
public class AdminGoodsController {
	
	private final AdminGoodsService adminGoodsService;
	
	private final HttpSession session;
	
	@GetMapping("/list")
    public String list(@RequestParam Map<String, Object> param, Model model) throws BaCdException {
		//관리자 아닌 사람 접근시 리다이렉트 처리 필요
        return "admin/goods/list";
    }
	
	@GetMapping("/api/summary")
	@ResponseBody
	public Map<String, Object> getSummary() {
		Common.adminIdCheck(session);
	    return adminGoodsService.getSummaryData();
	}
	
	@GetMapping("/ajaxList")
	@ResponseBody
	public Map<String, Object> ajaxList(@RequestParam(name="page", defaultValue="1") int page,
	                             @RequestParam(name="perPage", defaultValue="10") int perPage,
	                             @RequestParam(name="category", required=false) String category,
	                             @RequestParam(name="search", defaultValue="") String search,
	                             @RequestParam(name="status", required=false) String status,
	                             @RequestParam(name="stockStatus", required=false) String stockStatus,
	                             @RequestParam(name="isBanner", required=false) String isBanner,
	                             @RequestParam(name="minPrice", defaultValue="0") Long minPrice,
	                             @RequestParam(name="maxPrice", defaultValue="0") Long maxPrice,
	                             @RequestParam(name="startDate", required=false) String startDate,
	                             @RequestParam(name="endDate", required=false) String endDate,
	                             @RequestParam(name="sortDir", defaultValue="gno_desc") String sortDir) {
	    Common.adminIdCheck(session);
	    return adminGoodsService.goodsList(page, perPage, category, search, status, stockStatus, isBanner, minPrice, maxPrice, 
	    		startDate, endDate, sortDir);
	}
	
	@PostMapping("/ajaxModify")
	@ResponseBody
	public AjaxResponse ajaxModifyGoods(@RequestBody Map<String, Object> modifiedData) {
	    Common.adminIdCheck(session);
	    List<Map<String, Object>> updatedRows = (List<Map<String, Object>>) modifiedData.get("updatedRows");
	    
	    // AdminGoodsService에서 상품명, 가격, 재고, 상태값을 업데이트하는 로직 구현
	    return AjaxResponse.success(adminGoodsService.updateGoodsItems(updatedRows));
	}
}