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
import org.springframework.web.bind.annotation.RestController;

import com.project.app.bookmark.dto.BookmarkRequest;
import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.service.BookmarkService;
import com.project.app.common.AjaxResponse;
import com.project.app.schedule.dto.EventDto;
import com.project.app.schedule.dto.IdListDto;
import com.project.app.schedule.service.EventService;

@RequestMapping("/api/schedule")
@RestController
@Controller
public class EventController {
	
	@Autowired BookmarkService bookmarkService;
	@Autowired EventService eventService;
	
	@GetMapping("/getEvents")
	public List<EventDto> GetEvents() {
		
		List<EventDto> list = eventService.findAll();
		
		return list;
	}
	
	@PostMapping("/saveEvent")
	public AjaxResponse SaveEvent(@RequestBody EventDto dto) {
		
		EventDto eventDto = eventService.save(dto);
		
		return AjaxResponse.success(eventDto);  
	}	

	@DeleteMapping("/deleteEvent")
	public void DeleteEvent(@RequestBody IdListDto dto ) {
		
		System.out.println("ids : "+dto.getIds());
		
		eventService.deleteEvents(dto.getIds());
	}	
	
}
