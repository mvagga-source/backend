package com.project.app.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.app.auth.dto.LoginRequest;
import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.repository.MemberRepository;


@Service
public class MemberServiceImpl implements MemberService {
	
	@Autowired MemberRepository memberRepository;

	@Override // 전체회원정보
	public List<MemberDto> findAll() {
		
		// 정렬
//		Sort sort = Sort.by(
//				Sort.Order.desc("mdate"),
//				Sort.Order.asc("id")
//				);
//		List<MemberDto> list = memberRepository.findAll(sort);
		
		List<MemberDto> list = memberRepository.findAll(
				Sort.by(Sort.Order.desc("mdate"),Sort.Order.asc("id"))
				);
		
		
		return list;
	}

	@Override
	public MemberDto findByIdAndPw(MemberDto mDto) {
		
		MemberDto memberDto = memberRepository.findByIdAndPw(mDto.getId(),mDto.getPw()).orElse(null);
		
		return memberDto;
	}

}
