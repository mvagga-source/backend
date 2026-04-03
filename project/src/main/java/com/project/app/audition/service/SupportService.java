package com.project.app.audition.service;

import com.project.app.audition.dto.SupportMember;
import java.util.List;

public interface SupportService {
    // 후원하기 (저장 및 금액 업데이트)
    void saveSupport(Long supportId, String nickname, Long amount);
    
    // 특정 프로젝트의 후원 로그들 가져오기 (리액트 롤링 배너용)
    List<SupportMember> getLogsByProjectId(Long supportId);

	Object getProjectById(Long supportId);
}