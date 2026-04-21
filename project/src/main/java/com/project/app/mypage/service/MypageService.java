package com.project.app.mypage.service;

import java.util.List;
import java.util.Map;

import com.project.app.audition.dto.VoteDto;
import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.dto.BookmarkRequest;
import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.mypage.dto.MyRequestParams;
import com.project.app.mypage.dto.MyVoteResponse;

public interface MypageService {


	// 내 북마크 삭제
	void deleteBookmarkById(Long id);
	
	// 페이지별 내 북마크 리스트
	List<BookmarkDto> findMyPageBookmarks(BookmarkRequest dto);	
	
	// 내 북마크 리스트
	Map<String, Object> findMyBookmark(MyRequestParams params);	

	// 내 투표 현황
	List<Map<String, Object>> findMyVote(int page, int size, String startDate, String endDate);

	// 내 투표 삭제
	void deleteVoteById(Long id);

	// 내 주문 내역
	Map<String, Object> findMyOrders(MyRequestParams params);

	// 내 상품 내역
	Map<String, Object> findMyGoods(MyRequestParams params);

	// 내 판매 내역
	Map<String, Object> findMySale(MyRequestParams params);




 
  

}
