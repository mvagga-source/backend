package com.project.app.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.auth.dto.LoginRequest;
import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.repository.MemberRepository;
import com.project.app.common.exception.BaCdException;
import com.project.app.notificationSetting.dto.NotificationSettingDto;
import com.project.app.notificationSetting.repository.NotificationSettingRepository;


@Service
@Transactional(rollbackFor = BaCdException.class)
public class MemberServiceImpl implements MemberService {
	
	@Autowired MemberRepository memberRepository;
	
	@Autowired NotificationSettingRepository notificationSettingRepository;

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
	
	@Override
	public MemberDto findById(MemberDto mDto) {
		MemberDto memberDto = memberRepository.findById(mDto.getId()).orElse(null);
		return memberDto;
	}

	@Transactional
	@Override
	public MemberDto save(MemberDto mDto) throws BaCdException {
		MemberDto member = memberRepository.save(mDto);
		NotificationSettingDto defaultSetting = NotificationSettingDto.builder()
				.member(member) // @MapsId가 이 member의 ID를 PK로 사용함
				.allowBoardComment("y")
				.allowBoardLike("y")
				.allowGoodsReview("y")
				.allowGoodsReviewLike("y")
				.allowGoodsTrade("y")
				.allowQnaAnswer("y")
				.build();
		notificationSettingRepository.save(defaultSetting);
		return member;
	}

	@Override
	public Object findByNickname(MemberDto mDto) throws BaCdException {
		return memberRepository.findByNickname(mDto.getNickname()).orElse(null);
	}

}
