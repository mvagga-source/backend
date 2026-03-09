package com.project.app.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.app.user.dto.LoginRequest;
import com.project.app.user.dto.Member;
import com.project.app.user.repository.MemberRepository;


@Service
public class MemberServiceImpl implements MemberService {
	
	@Autowired MemberRepository memberRepository;

	@Override // 전체회원정보
	public List<Member> findAll() {
		
		// 정렬
//		Sort sort = Sort.by(
//				Sort.Order.desc("mdate"),
//				Sort.Order.asc("id")
//				);
//		List<MemberDto> list = memberRepository.findAll(sort);
		
		List<Member> list = memberRepository.findAll(
				Sort.by(Sort.Order.desc("mdate"),Sort.Order.asc("id"))
				);
		
		
		return list;
	}

	@Override
	public Member findByIdAndPw(Member mDto) {
		
		Member memberDto = memberRepository.findByIdAndPw(mDto.getId(),mDto.getPw()).orElse(null);
		
		return memberDto;
	}

}
