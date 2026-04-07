package com.project.app.admin.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.admin.service.AdminIdeaService;
import com.project.app.admin.service.AdminReportService;
import com.project.app.common.exception.BaCdException;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/community/report")
@RequiredArgsConstructor
public class AdminReportController {
private final AdminReportService adminReportService;
	
	@Value("${img.host.url}")
    private String imgHostUrl;
	
	@GetMapping("/list")
    public String list(@RequestParam Map<String, Object> param, Model model) throws BaCdException {
        return "admin/report/list";
    }
	
	// 상세페이지 이동 (단건 조회)
    @GetMapping("/view")
    public String view(@RequestParam("repono") Long repono, Model model) throws BaCdException {
    	model.addAttribute("hostUrl", imgHostUrl);
        model.addAttribute("idea", adminReportService.view(repono));
        return "admin/report/view";
    }
	
	/**
     * 신고 목록 조회 (필터 및 등록일 검색 포함)
     */
    @GetMapping("/ajaxList")
    @ResponseBody
    public Map<String, Object> ajaxList(
            @RequestParam(name="page", defaultValue="1") int page,
            @RequestParam(name="perPage", defaultValue="10") int perPage,
            @RequestParam(name="reportType", required=false) String reportType, // 신고유형
            @RequestParam(name="category", required=false) String category,    // 검색필터(제목/작성자 등)
            @RequestParam(name="search", defaultValue="") String search,
            @RequestParam(name="status", defaultValue="") String status,
            @RequestParam(name="startDate", defaultValue="") String startDate,
            @RequestParam(name="endDate", defaultValue="") String endDate) {
        
        return adminReportService.ajaxList(page, perPage, reportType, category, search, status, startDate, endDate);
    }
}
