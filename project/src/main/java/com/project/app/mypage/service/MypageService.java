package com.project.app.mypage.service;

import java.util.List;
import java.util.Map;

import com.project.app.audition.dto.VoteDto;
import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.mypage.dto.MyVoteResponse;

public interface MypageService {

	// 북마크 전체 정보
	List<ResponseBookmark> findAll();

	void deleteById(Long id);

	// 팬별 투표 현황
	List<Map<String, Object>> findById(int page, int size);  

}
