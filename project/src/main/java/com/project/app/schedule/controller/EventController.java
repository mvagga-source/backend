package com.project.app.schedule.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.bookmark.dto.BookmarkRequest;
import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.service.BookmarkService;
import com.project.app.schedule.dto.EventDto;
import com.project.app.schedule.service.EventService;

@RequestMapping("/api/schedule")
@CrossOrigin(origins = "http://localhost:3000") // React 포트 허용
@Controller
public class EventController {
	
	@Autowired BookmarkService bookmarkService;
	@Autowired EventService eventService;
	
	@ResponseBody
	@GetMapping("/getEvents")
	public List<EventDto> GetEvents() {
		
		List<EventDto> list = eventService.findAll();
		
		return list;
	}


	/*
	 * 
	 * MyPage API
	 * 
	 */
	

	
//	@ResponseBody
//	@GetMapping("/Mypage/getBookmarkPage")
//	public Page<BookmarkDto> GetBookmarkPage(Pageable pageable) {
//		
//		Page<BookmarkDto> list = bookmarkService.findAll(pageable);
//		
//		return list;
//	}
//	
//	@ResponseBody
//	@DeleteMapping("/Mypage/deleteMyBookmark/{id}")
//	public void DeleteMyBookmark(@PathVariable("id") Long id) {
//		
//		System.out.println("DeleteMyBookmark id : "+id);
//		
//		bookmarkService.deleteById(id);
//		
////		return ResponseEntity.ok().build();
//	}		
	
}
