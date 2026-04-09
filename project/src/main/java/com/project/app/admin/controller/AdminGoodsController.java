package com.project.app.admin.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.admin.service.AdminGoodsService;
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
}