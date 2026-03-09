package com.project.app.user.controller;


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

import com.project.app.user.dto.LoginRequest;
import com.project.app.user.dto.Member;
import com.project.app.user.service.MemberService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // React 포트 허용
public class MController {

	// java -jar build/libs/project-0.0.1-SNAPSHOT.jar

	@Autowired MemberService memberService;
	@Autowired HttpSession session;


	@GetMapping("/auth/list")
	public List<Member> mlist() {

		System.out.println("test");

		List<Member> list = memberService.findAll();
		return list;
	}

	@ResponseBody
	@PostMapping("/auth/login")
	public ResponseEntity<?> mlogin(@RequestBody Member mDto){

		Member memberDto = memberService.findByIdAndPw(mDto);

        if(memberDto == null){
            return ResponseEntity.status(401).body("login fail");
        }

	    session.setAttribute("session_id", memberDto.getId());
	    session.setAttribute("session_name", memberDto.getName());

	    return ResponseEntity.ok(memberDto);
	}

	@ResponseBody
	@PostMapping("/auth/logout")
	public String mlogout(){

		session.invalidate();
		return "true";
	}


    @GetMapping("/auth/userSession")
    public ResponseEntity<?> me(HttpSession session) {
        String session_id = (String)session.getAttribute("session_id");
        if (session_id == null) {
            //return ResponseEntity.status(401).body("NOT_LOGIN");
        	return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(session_id);
    }

}
