package com.project.app.auth.service;

import java.util.List;

import com.project.app.auth.dto.LoginRequest;
import com.project.app.auth.dto.MemberDto;
import com.project.app.common.exception.BaCdException;

public interface MemberService {


	//전체회원정보
	List<MemberDto> findAll();

	public MemberDto findById(MemberDto mDto);
	
	MemberDto findByIdAndPw(MemberDto mDto) throws BaCdException;

	public MemberDto save(MemberDto mDto) throws BaCdException;

	public Object findByNickname(MemberDto mDto) throws BaCdException;
}
