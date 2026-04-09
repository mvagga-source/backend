package com.project.app.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.admin.service.AdminNoticeService;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {
	
	private final AdminNoticeService adminNoticeService;
	
	private final HttpSession session;

	@GetMapping("/list")
    public String list(@RequestParam Map<String, Object> param, Model model) throws BaCdException {
        return "admin/notice/list";
    }
	
	/**
     * 공지사항 목록 AJAX 조회 (Toast UI Grid 전용)
     */
    @GetMapping("/ajaxList")
    @ResponseBody
    public Map<String, Object> ajaxList(@RequestParam(name="page", defaultValue="1") int page,
            @RequestParam(name="perPage", defaultValue="10") int perPage,
            @RequestParam(name="category", required=false, defaultValue="") String category,
            @RequestParam(name="search", defaultValue="") String search,
            @RequestParam(name="startDate", defaultValue="") String startDate,
            @RequestParam(name="endDate", defaultValue="") String endDate,
            @RequestParam(name="sortBy", defaultValue="DESC") String sortBy,
            @RequestParam(name="sortDir", defaultValue="DESC") String sortDir) {
    	Common.adminIdCheck(session);
        return (Map<String, Object>) adminNoticeService.ajaxList(page, perPage, category, search, sortDir, sortBy, startDate, endDate);
    }
    
    /**
     * 공지사항 그리드 데이터 일괄 저장 (생성/수정/삭제)
     * Toast UI Grid의 modifyData(grid.save()) 대응
     */
    @PostMapping("/ajaxModify")
    @ResponseBody
    public Map<String, Object> ajaxModify(@RequestBody Map<String, Object> param) {
    	Common.adminIdCheck(session);
        // params 내부에는 createdRows, updatedRows, deletedRows 리스트가 포함되어 전송됨
        return AjaxResponse.success(adminNoticeService.saveAll(param));
    }

    /**
     * 체크박스 선택 행 일괄 삭제
     */
    @PostMapping("/ajaxDelete")
    @ResponseBody
    public Map<String, Object> ajaxDelete(@RequestBody Map<String, Object> param) {
    	Common.adminIdCheck(session);
        return AjaxResponse.success(adminNoticeService.deleteAll(param));
    }
}
