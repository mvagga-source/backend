package com.project.app.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.admin.service.AdminBoardCommentService;
import com.project.app.admin.service.AdminBoardService;
import com.project.app.admin.service.AdminIdeaService;
import com.project.app.admin.service.AdminReportService;
import com.project.app.board.dto.BoardDto;
import com.project.app.boardcomment.dto.BoardCommentDto;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/community/report")
@RequiredArgsConstructor
public class AdminReportController {
private final AdminReportService adminReportService;
	
	@Value("${img.host.url}")
    private String imgHostUrl;
	
	@Value("${server.host}")
	private String serverHost;
	
	@Value("${server.port}")
	private String serverPort;
	
	private final AdminBoardCommentService adminBoardCommentService;
	
	private final AdminBoardService adminBoardService;
	
	private final HttpSession session;
	
	@GetMapping("/list")
    public String list(@RequestParam Map<String, Object> param, Model model) throws BaCdException {
        return "admin/community/report/list";
    }
	
	// 상세페이지 이동 (단건 조회)
    @GetMapping("/view")
    public String view(@RequestParam("repono") Long repono, Model model) throws BaCdException {
    	model.addAttribute("serverHost", serverHost);
    	model.addAttribute("hostUrl", imgHostUrl);
        model.addAttribute("report", adminReportService.view(repono));
        return "admin/community/report/view";
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
    	Common.adminIdCheck(session);
        return adminReportService.ajaxList(page, perPage, reportType, category, search, status, startDate, endDate);
    }
    
    /**
     * 상세 신고처리 저장 API
     */
    @PostMapping("/ajaxUpdateStatus")
    @ResponseBody
    public Map<String, Object> ajaxUpdateStatus(@RequestParam("repono") Long repono,
    		@RequestParam("status") String status,
    		@RequestParam(name="targetIdName", required=false) String targetIdName,
            @RequestParam(name="targetId", required=false) Long targetId
    		) throws BaCdException {
    	Common.adminIdCheck(session);
        return AjaxResponse.success(adminReportService.ajaxUpdateStatus(repono, status, targetIdName, targetId));
    }
    
    /**
     * 댓글 원본보기
     * @param targetType
     * @param targetId
     * @return
     */
    @GetMapping("/ajaxGetOrigin")
    @ResponseBody
    public AjaxResponse ajaxGetOrigin(@RequestParam(name="targetIdName", required=false) String targetIdName, @RequestParam(name="targetId", required=false) Long targetId) {
    	Common.adminIdCheck(session);
        Map<String, Object> result = new HashMap<>();
        try {
            if ("bno".equals(targetIdName)) {
                BoardDto board = adminBoardService.findById(targetId);
                if(board != null) {
                    result.put("success", true);
                    result.put("data", board);
                }
            } else if ("cno".equals(targetIdName)) {
                BoardCommentDto comment = adminBoardCommentService.findById(targetId);
                if(comment != null) {
                    result.put("success", true);
                    result.put("data", comment);
                }
            }
        } catch (Exception e) {
            result.put("success", false);
        }
        return AjaxResponse.success(result);
    }
}
