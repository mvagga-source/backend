package com.project.app.admin.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.admin.service.AdminGoodsService;
import com.project.app.admin.service.AdminIdeaService;
import com.project.app.admin.service.AdminQnaService;
import com.project.app.common.Common;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/community/idea")
@RequiredArgsConstructor
public class AdminIdeaController {
	private final AdminIdeaService adminIdeaService;
	
	@Value("${img.host.url}")
    private String imgHostUrl;
	
	private final HttpSession session;
	
	@GetMapping("/list")
    public String list(@RequestParam Map<String, Object> param, Model model) throws BaCdException {
        return "admin/community/idea/list";
    }
	
	// 상세페이지 이동 (단건 조회)
    @GetMapping("/view")
    public String view(@RequestParam("ideano") Long ideano, Model model) throws BaCdException {
        // 서비스에서 DTO를 가져와 모델에 바인딩
    	model.addAttribute("hostUrl", imgHostUrl);
        model.addAttribute("idea", adminIdeaService.view(ideano));
        return "admin/community/idea/view";
    }
	
	/**
     * Idea 목록 조회 (필터 및 등록일 검색 포함)
     */
    @GetMapping("/ajaxList")
    @ResponseBody
    public Map<String, Object> ajaxList(
            @RequestParam(name="page", defaultValue="1") int page,
            @RequestParam(name="perPage", defaultValue="10") int perPage,
            @RequestParam(name="category", required=false) String category,
            @RequestParam(name="ideacategory", required=false) String ideacategory,
            @RequestParam(name="search", defaultValue="") String search,
            @RequestParam(name="status", defaultValue="") String status,
            @RequestParam(name="startDate", defaultValue="") String startDate,
            @RequestParam(name="endDate", defaultValue="") String endDate) {
    	Common.adminIdCheck(session);
        return adminIdeaService.ajaxList(page, perPage, category, ideacategory, search, status, startDate, endDate);
    }
    
    /**
     * 관리자 검토 의견 저장 (Ajax)
     */
    @PostMapping("/ajaxSaveFeedback")
    @ResponseBody
    public Map<String, Object> ajaxSaveFeedback(@RequestParam Map<String, Object> param) {
    	Common.adminIdCheck(session);
        return adminIdeaService.saveFeedback(param);
    }
}
