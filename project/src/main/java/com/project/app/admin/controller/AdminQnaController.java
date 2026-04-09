package com.project.app.admin.controller;

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
import com.project.app.admin.service.AdminQnaService;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/community/qna")
@RequiredArgsConstructor
public class AdminQnaController {
	
	private final AdminQnaService adminQnaService;
	
	private final HttpSession session;
	
	@GetMapping("/list")
    public String list(@RequestParam Map<String, Object> param, Model model) throws BaCdException {
		//관리자 아닌 사람 접근시 리다이렉트 처리 필요
        return "admin/community/qna/list";
    }
	
	// 상세페이지 이동 (단건 조회)
    @GetMapping("/view")
    public String view(@RequestParam("qno") Long qno, Model model) throws BaCdException {
    	//관리자 아닌 사람 접근시 리다이렉트 처리 필요
        // 서비스에서 DTO를 가져와 모델에 바인딩
        model.addAttribute("qna", adminQnaService.view(qno));
        return "admin/community/qna/view";
    }
	
	/**
     * QnA 목록 조회 (필터 및 등록일 검색 포함)
     */
    @GetMapping("/ajaxList")
    @ResponseBody
    public Map<String, Object> ajaxList(
            @RequestParam(name="page", defaultValue="1") int page,
            @RequestParam(name="perPage", defaultValue="10") int perPage,
            @RequestParam(name="category", required=false) String category,
            @RequestParam(name="search", defaultValue="") String search,
            @RequestParam(name="status", defaultValue="") String status,
            @RequestParam(name="startDate", defaultValue="") String startDate,
            @RequestParam(name="endDate", defaultValue="") String endDate) {
    	Common.adminIdCheck(session);
        return adminQnaService.ajaxList(page, perPage, category, search, status, startDate, endDate);
    }

    /**
     * 상세 팝업에서 호출하는 답변 저장 API
     */
    @PostMapping("/ajaxSaveReply")
    @ResponseBody
    public Map<String, Object> ajaxSaveReply(@RequestParam Map<String, Object> param) {
    	Common.adminIdCheck(session);
        return AjaxResponse.success(adminQnaService.saveReply(param));
    }

    /**
     * 체크박스 선택 행 삭제
     */
    @PostMapping("/ajaxDelete")
    @ResponseBody
    public Map<String, Object> ajaxDelete(@RequestBody Map<String, Object> param) {
    	Common.adminIdCheck(session);
        return AjaxResponse.success(adminQnaService.deleteAll(param));
    }
}