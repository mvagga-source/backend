package com.project.app.user.service;

import java.util.List;

import com.project.app.user.dto.LoginRequest;
import com.project.app.user.dto.MemberDto;

public interface MemberService {


	//전체회원정보
	List<MemberDto> findAll();

	MemberDto findByIdAndPw(MemberDto mDto);


}
