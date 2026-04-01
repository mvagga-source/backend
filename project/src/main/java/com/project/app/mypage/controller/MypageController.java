package com.project.app.mypage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.audition.dto.VoteDto;
import com.project.app.audition.service.VoteService;
import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.common.AjaxResponse;
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
	
	
	@GetMapping("/getBookmarks")
	public List<ResponseBookmark> GetBookmarkPage() {
		
		List<ResponseBookmark> list = mypageService.findAll();
		
		return list;
	}
	
	@DeleteMapping("/deleteBookmark/{id}")
	public void DeleteMyBookmark(@PathVariable("id") Long id) {
		
		System.out.println("DeleteMyBookmark id : "+id);
		
		mypageService.deleteBookmarkById(id);
	}	
	
	@GetMapping("/getMyVotePage")
	public AjaxResponse GetMyVotePage(
	        @RequestParam(name="page", defaultValue="1") int page,
	        @RequestParam(name="size", defaultValue="10") int size,
	        @RequestParam(name="startDate") String startDate,
	        @RequestParam(name="endDate") String endDate
			) {
		
		System.out.println("startDate : "+startDate);
		System.out.println("endDate : "+endDate);

		List<Map<String, Object>> list  = mypageService.findById(page, size, startDate, endDate);
		
		return AjaxResponse.success(list);  
	}
	
	@DeleteMapping("/deleteMyVote/{id}")
	public AjaxResponse DeleteMyVote(@PathVariable("id") Long id) {
		
		System.out.println("DeleteMyVote id : "+id);
		
		mypageService.deleteVoteById(id);
		return AjaxResponse.success();
	}		

}
