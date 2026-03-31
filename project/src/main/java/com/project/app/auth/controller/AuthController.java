package com.project.app.auth.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.auth.dto.LoginRequest;
import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.service.MemberService;
import com.project.app.common.AjaxResponse;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	// java -jar build/libs/project-0.0.1-SNAPSHOT.jar
	
	@Autowired MemberService memberService;
	@Autowired HttpSession session;
	

	@GetMapping("/list")
	public List<MemberDto> mlist() {
		
		System.out.println("test");
		
		List<MemberDto> list = memberService.findAll();
		return list;
	}
	
	@ResponseBody
	@PostMapping("/login")
	public ResponseEntity<?> mlogin(@RequestBody MemberDto mDto){
		
		MemberDto memberDto = memberService.findByIdAndPw(mDto);
		
        if(memberDto == null){
            return ResponseEntity.status(401).body("login fail");
        }

	    session.setAttribute("user", memberDto);

	    return ResponseEntity.ok(memberDto);	
	}
	
	@ResponseBody
	@PostMapping("/checkId")
	public AjaxResponse checkId(@RequestBody MemberDto mDto){
		//아이디 중복체크
		if(memberService.findById(mDto) != null) {
			throw new BaCdException(ErrorCode.USERID_DUPLICATE);
		}
	    return AjaxResponse.success();	
	}
	
	@ResponseBody
	@PostMapping("/checkNickname")
	public AjaxResponse checkNickname(@RequestBody MemberDto mDto){
		//닉네임 중복체크
		if(memberService.findByNickname(mDto) != null) {
			throw new BaCdException(ErrorCode.NICKNAME_DUPLICATE);
		}
	    return AjaxResponse.success();	
	}
	
	@ResponseBody
	@PostMapping("/signup")
	public AjaxResponse join(@RequestParam(name="pw2", required=false) String pw2,
			@RequestBody MemberDto mDto){
		if(mDto.getId() == null || !mDto.getId().matches("^[a-z0-9]{4,16}$")) {
		    throw new BaCdException(ErrorCode.USERID_INVALID);
		}
		//아이디 중복체크
		if(memberService.findById(mDto) != null) {
			throw new BaCdException(ErrorCode.USERID_DUPLICATE);
		}
		// 비밀번호 체크
		if(mDto.getPw() == null || mDto.getPw().isEmpty()) {
		    throw new BaCdException(ErrorCode.PASSWORD_EMPTY, ErrorCode.PASSWORD_EMPTY.getMessage());
		}
		// 비밀번호 확인
		if(!mDto.getPw().equals(pw2)) {
		    throw new BaCdException(ErrorCode.PASSWORD_MISMATCH);
		}
		// 이메일 체크
		if(mDto.getEmail() == null || mDto.getEmail().isEmpty()) {
		    throw new BaCdException(ErrorCode.EMAIL_EMPTY);
		}
		if (!mDto.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
	        throw new BaCdException(ErrorCode.EMAIL_EMPTY, "올바른 이메일 형식이 아닙니다.");
	    }
		// --- 닉네임, 휴대전화 Empty 체크 ---
	    if (mDto.getNickname() == null || mDto.getNickname().isEmpty()) {
	    	throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "닉네임을 입력해주세요.");
	    }
		memberService.save(mDto);
		return AjaxResponse.success();
	}
	
	@ResponseBody
	@PostMapping("/logout")
	public String mlogout(){

		session.invalidate();
		return "true";
	}	
	
	@ResponseBody
    @GetMapping("/userSession")
    public ResponseEntity<?> userSession() {
		
		MemberDto memberDto = (MemberDto) session.getAttribute("user");
        
        if (memberDto == null) {
        	return ResponseEntity.ok(null);
        }
        
        return ResponseEntity.ok(memberDto);
    }
	
}
