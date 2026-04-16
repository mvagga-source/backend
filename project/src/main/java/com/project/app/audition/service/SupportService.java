package com.project.app.audition.service;

import com.project.app.audition.dto.SupportMember;
import com.project.app.audition.dto.SupportOrderDto;

import java.util.List;
import java.util.Map;

public interface SupportService {
    // 후원하기 (저장 및 금액 업데이트)
    void saveSupport(Long supportId, String nickname, Long amount);
    
    // 특정 프로젝트의 후원 로그들 가져오기 (리액트 롤링 배너용)
    List<SupportMember> getLogsByProjectId(Long supportId);

	Object getProjectById(Long supportId);

	
	//카카오페이
	Map<String, Object> readyPayment(SupportOrderDto orderDto);// 결제 준비

	Map<String, Object> approvePayment(String pgToken, String tid);// 결제 승인
}