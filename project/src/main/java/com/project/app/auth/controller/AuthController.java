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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.auth.dto.LoginRequest;
import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.service.MemberService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // React 포트 허용
public class AuthController {
	
	// java -jar build/libs/project-0.0.1-SNAPSHOT.jar
	
	@Autowired MemberService memberService;
	@Autowired HttpSession session;
	

	@GetMapping("/auth/list")
	public List<MemberDto> mlist() {
		
		System.out.println("test");
		
		List<MemberDto> list = memberService.findAll();
		return list;
	}
	
	@ResponseBody
	@PostMapping("/auth/login")
	public ResponseEntity<?> mlogin(@RequestBody MemberDto mDto){
		
		MemberDto memberDto = memberService.findByIdAndPw(mDto);
		
        if(memberDto == null){
            return ResponseEntity.status(401).body("login fail");
        }

	    session.setAttribute("user", memberDto);

	    return ResponseEntity.ok(memberDto);	
	}
	
	@ResponseBody
	@PostMapping("/auth/logout")
	public String mlogout(){

		session.invalidate();
		return "true";
	}	
	
	@ResponseBody
    @GetMapping("/auth/userSession")
    public ResponseEntity<?> userSession() {
		
		MemberDto memberDto = (MemberDto) session.getAttribute("user");
        
        if (memberDto == null) {
        	return ResponseEntity.ok(null);
        }
        
        return ResponseEntity.ok(memberDto);
    }
	
}
