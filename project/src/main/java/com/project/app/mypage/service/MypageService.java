package com.project.app.mypage.service;

import java.util.List;
import java.util.Map;

import com.project.app.audition.dto.VoteDto;
import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.mypage.dto.MyRequestParams;
import com.project.app.mypage.dto.MyVoteResponse;

public interface MypageService {

	// 북마크 전체 정보
	List<ResponseBookmark> findAll();
	
	// 내 북마크 삭제
	void deleteBookmarkById(Long id);	

	// 팬별 투표 현황
	List<Map<String, Object>> findMyVote(int page, int size, String startDate, String endDate);

	// 내 투표 삭제
	void deleteVoteById(Long id);

	// 내 주문 내역
	Map<String, Object> findMyOrders(MyRequestParams params);

 
  

}
