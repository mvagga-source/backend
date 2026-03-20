package com.project.app.boardlike.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.service.MemberService;
import com.project.app.board.dto.BoardDto;
import com.project.app.board.service.BoardService;
import com.project.app.boardlike.dto.BoardLikeDto;
import com.project.app.boardlike.service.BoardLikeService;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/boardlike")
public class BoardLikeController {
	
	@Autowired
	BoardLikeService boardLikeService;
	
	@Autowired
	MemberService memberService;

	@Autowired
	HttpSession session;
	
	@ResponseBody
	@PostMapping("/save")
	public AjaxResponse save(@RequestBody BoardLikeDto lbdto, Model model) {
		MemberDto memberDto = Common.idCheck(session);
		return AjaxResponse.success().message(boardLikeService.save(lbdto, memberDto));
	}
}
