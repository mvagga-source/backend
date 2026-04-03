package com.project.app.admin.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.app.common.exception.BaCdException;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

	@GetMapping("/list")
    public String list(@RequestParam Map<String, Object> param, Model model) throws BaCdException {
		/*if (true) { 
	        throw new RuntimeException("에러 페이지 테스트를 위한 강제 예외 발생입니다."); 
	    }*/
        return "admin/notice/list";
    }
}
