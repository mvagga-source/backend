package com.project.app.mypage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.audition.dto.VoteDto;
import com.project.app.audition.service.VoteService;
import com.project.app.auth.dto.MemberDto;
import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.dto.BookmarkRequest;
import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.mypage.dto.MyRequestParams;
import com.project.app.mypage.dto.MyVoteResponse;
import com.project.app.mypage.service.MypageService;
import com.project.app.video.service.VideoService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@RestController
@Controller
public class MypageController {
	
	
	private final MypageService mypageService;
	
	
    /**
     * 나의 북마크 삭제
     */	
	@DeleteMapping("/deleteBookmark/{id}")
	public void DeleteMyBookmark(@PathVariable("id") Long id) {
		
		System.out.println("DeleteMyBookmark id : "+id);
		
		mypageService.deleteBookmarkById(id);
	}	
	
    /**
     * 페이지별 나의 북마크 내역 조회
     */	
	@GetMapping("/getMyPageBookmarks")
	public List<BookmarkDto> getMyPageBookmarks(BookmarkRequest dto) {
		
		List<BookmarkDto> list = mypageService.findMyPageBookmarks(dto);
		
		return list;
	}	
	
	
    /**
     * 나의 북마크 내역 조회
     */		
	@GetMapping("/getMyBookmarkPage")
	public AjaxResponse getMyBookmarkPage(MyRequestParams params) {
		
		Map<String, Object> map = mypageService.findMyBookmark(params);
		
		return AjaxResponse.success(map);  
	}		
	
	
    /**
     * 나의 투표 내역 조회
     */	
	@GetMapping("/getMyVotePage")
	public AjaxResponse GetMyVotePage(
	        @RequestParam(name="page", defaultValue="1") int page,
	        @RequestParam(name="size", defaultValue="10") int size,
	        @RequestParam(name="startDate") String startDate,
	        @RequestParam(name="endDate") String endDate
			) {
		
		System.out.println("startDate : "+startDate);
		System.out.println("endDate : "+endDate);

		List<Map<String, Object>> list  = mypageService.findMyVote(page, size, startDate, endDate);
		
		return AjaxResponse.success(list);  
	}
	
    /**
     * 나의 투표 내역 삭제
     */	
	@DeleteMapping("/deleteMyVote/{id}")
	public AjaxResponse DeleteMyVote(@PathVariable("id") Long id) {
		
		System.out.println("DeleteMyVote id : "+id);
		
		mypageService.deleteVoteById(id);
		return AjaxResponse.success();  
	}		
	
    /**
     * 나의 주문 내역 조회
     */
    @GetMapping("/getMyOrderPage")
    public AjaxResponse getMyOrderPage(MyRequestParams params) {
    	
    	System.out.println("params : "+params);
        
        Map<String, Object> map = mypageService.findMyOrders(params);
        
        return AjaxResponse.success(map);
    }
    
    /**
     * 나의 판매 내역 조회
     */
    @GetMapping("/getMySalePage")
    public AjaxResponse getMySalePage(MyRequestParams params) {
    	
    	System.out.println("params : "+params);
        
        Map<String, Object> map = mypageService.findMySale(params);
        
        return AjaxResponse.success(map);
    }  
    
    /**
     * 나의 주문 내역 조회
     */
    @GetMapping("/getMyGoodsPage")
    public AjaxResponse getMyGoodsPage(MyRequestParams params) {
    	
    	System.out.println("params : "+params);
        
        Map<String, Object> map = mypageService.findMyGoods(params);
        
        return AjaxResponse.success(map);
    }    

}
