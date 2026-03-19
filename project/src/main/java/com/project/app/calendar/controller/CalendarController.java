package com.project.app.calendar.controller;

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

import com.project.app.calendar.dto.Bookmark;
import com.project.app.calendar.dto.BookmarkDto;
import com.project.app.calendar.dto.EventDto;
import com.project.app.calendar.service.BookmarkService;
import com.project.app.calendar.service.EventService;

@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // React 포트 허용
@Controller
public class CalendarController {
	
	@Autowired BookmarkService bookmarkService;
	@Autowired EventService eventService;
	
	@ResponseBody
	@GetMapping("/Calendar/getEvent")
	public List<EventDto> GetEvent() {
		
		List<EventDto> list = eventService.findAll();
		
		return list;
	}
	
	@ResponseBody
	@PostMapping("/Calendar/getMyBookmark")
	public List<Long> GetBookmark(@RequestBody Map<String,String> map) {
		
		List<Long> list = bookmarkService.findEventIdsByUserId(map.get("userId"));
		
		return list;
	}	
	
	
	// 북마크 추가 또는 삭제
	@ResponseBody
	@PostMapping("/Calendar/toggleBookmark")
    public boolean ToggleBookmark(@RequestBody Bookmark dto)
	{
        return bookmarkService.toggleBookmark(dto);
    }
	

	/*
	 * 
	 * MyPage API
	 * 
	 */
	
	@ResponseBody
	@GetMapping("/Mypage/getEventPage")
	public Page<EventDto> GetEventPage(Pageable pageable) {
		
		Page<EventDto> list = eventService.findAll(pageable);
		
		return list;
	}	
	
	
	@ResponseBody
	@GetMapping("/Mypage/getBookmarkPage")
	public Page<BookmarkDto> GetBookmarkPage(Pageable pageable) {
		
		Page<BookmarkDto> list = bookmarkService.findAll(pageable);
		
		return list;
	}
	
	@ResponseBody
	@DeleteMapping("/Mypage/deleteMyBookmark/{id}")
	public void DeleteMyBookmark(@PathVariable("id") Long id) {
		
		System.out.println("DeleteMyBookmark id : "+id);
		
		bookmarkService.deleteById(id);
		
//		return ResponseEntity.ok().build();
	}		
	
}
