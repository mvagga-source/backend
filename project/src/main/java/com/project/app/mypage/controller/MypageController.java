package com.project.app.mypage.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.mypage.service.MypageService;
import com.project.app.video.service.VideoService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/mypage")
@CrossOrigin(origins = "http://localhost:3000") // React 포트 허용
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
		
		mypageService.deleteById(id);
	}		

}
