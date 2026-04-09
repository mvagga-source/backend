package com.project.app.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.admin.service.AdminGoodsOrdersService;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.goodsorders.dto.GoodsOrdersDto;
import com.project.app.goodsorders.service.GoodsOrdersService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminGoodsOrdersController {
	private final AdminGoodsOrdersService adminGoodsOrdersService;
	
	private final HttpSession session;
	
	@GetMapping("/ajaxList")
	@ResponseBody
	public AjaxResponse ajaxList(@RequestParam(name="page", defaultValue="1") int page,
            @RequestParam(name="perPage", defaultValue="10") int perPage,
            @RequestParam(name="category", required=false, defaultValue="") String category,
            @RequestParam(name="search", defaultValue="") String search,
            @RequestParam(name="status", required=false, defaultValue="") String status,
            @RequestParam(name="delivStatus", required=false, defaultValue="") String delivStatus,
            @RequestParam(name="minPrice", defaultValue="0") int minPrice,
            @RequestParam(name="maxPrice", defaultValue="0") int maxPrice,
            @RequestParam(name="settleYn", defaultValue="n") String settleYn,
            @RequestParam(name="startDate", defaultValue="") String startDate,
            @RequestParam(name="endDate", defaultValue="") String endDate,
            @RequestParam(name="sortBy", defaultValue="DESC") String sortBy,
            @RequestParam(name="sortDir", defaultValue="DESC") String sortDir) {
		Common.adminIdCheck(session);
	    return AjaxResponse.success(adminGoodsOrdersService.list(page, perPage, minPrice, maxPrice, settleYn, delivStatus,
                category, status, search, sortDir, sortBy, startDate, endDate));
	}
	
	@PostMapping("/ajaxModify")
	@ResponseBody
	public Map<String, Object> ajaxModify(@RequestBody Map<String, Object> modifiedData) {
	    // 관리자 체크
	    Common.adminIdCheck(session);
        List<Map<String, Object>> updatedRows = (List<Map<String, Object>>) modifiedData.get("updatedRows");
        
        Map<String, Object> map = new HashMap<>();
        if (updatedRows != null && !updatedRows.isEmpty()) {
        	map = adminGoodsOrdersService.updateOrders(updatedRows);
        }
        
        return AjaxResponse.success(map);
	}
}
