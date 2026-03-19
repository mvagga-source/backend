package com.project.app.auth.service;

import java.util.List;

import com.project.app.auth.dto.LoginRequest;
import com.project.app.auth.dto.MemberDto;

public interface MemberService {


	//전체회원정보
	List<MemberDto> findAll();

	MemberDto findByIdAndPw(MemberDto mDto);


}
