package com.project.app.admin.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.app.admin.service.AdminAuditionService;
import com.project.app.common.exception.BaCdException;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

	@GetMapping("/list.do")
    public String list(@RequestParam Map<String, Object> param, Model model) throws BaCdException {
        return "admin/notice/list";
    }
}
