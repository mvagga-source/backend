package com.project.app.boardcomment.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.auth.dto.MemberDto;
import com.project.app.boardcomment.dto.BoardCommentDto;
import com.project.app.boardcomment.service.BoardCommentService;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/comment")
public class BoardCommentController {

	@Autowired
	private BoardCommentService commentService;

	@Autowired
	private HttpSession session;

	@GetMapping("/list")
	@ResponseBody
	public AjaxResponse commentList(
			@RequestParam(name="bno", required=false, defaultValue="") Long bno,
			@RequestParam(name="size", required=false, defaultValue="10") int size,
			@RequestParam(name="lastCno", required=false, defaultValue="0") Long lastCno,
			Model model) {
		Map<String, Object> map = commentService.findAll(size, bno, lastCno);
		return AjaxResponse.success(map);
	}

	@PostMapping("/save")
	@ResponseBody
	public AjaxResponse commentSave(
			//BoardDto bdto, MemberDto mdto,
			@RequestParam(name="bno", required=false, defaultValue="") Long bno,
			@RequestParam(name="id", required=false, defaultValue="") String id,
			BoardCommentDto cdto,
			Model model) {
		MemberDto memberDto = Common.idCheck(session);
		if("".equals(cdto.getCcontent())) {
			throw new BaCdException(ErrorCode.INPUT_EMPTY, "내용을 입력해주세요.");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("comment", commentService.save(bno, memberDto, cdto));
		return AjaxResponse.success(map);
	}

	@PostMapping("/update")
	@ResponseBody
	public AjaxResponse commentUpdate(BoardCommentDto cdto, Model model) {
		MemberDto memberDto = Common.idCheck(session);		//작성자가 맞는지 확인로직 필요
		if("".equals(cdto.getCcontent())) {
			throw new BaCdException(ErrorCode.INPUT_EMPTY, "내용을 입력해주세요.");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("comment", commentService.update(cdto, memberDto));
		return AjaxResponse.success(map);
	}

	@PostMapping("/delete")
	@ResponseBody
	public AjaxResponse commentDelete(BoardCommentDto cdto, Model model) {
		MemberDto memberDto = Common.idCheck(session);		//작성자가 맞는지 확인로직 필요
		commentService.deleteById(cdto, memberDto);
		return AjaxResponse.success();
	}
}
