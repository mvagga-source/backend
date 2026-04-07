package com.project.app.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.app.admin.dto.schedule.ScheduleRequestParams;
import com.project.app.admin.service.AdminScheduleService;
import com.project.app.admin.service.AdminVideoService;
import com.project.app.audition.dto.IdolProfileDto;
import com.project.app.audition.service.IdolProfileService;
import com.project.app.common.AjaxResponse;
import com.project.app.schedule.dto.EventDto;
import com.project.app.schedule.dto.IdListDto;
import com.project.app.video.dto.VideoRequestParams;
import com.project.app.video.service.VideoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/schedule")
@RequiredArgsConstructor
public class AdminScheduleController {
	
	private final AdminScheduleService adminScheduleService;

	@GetMapping("/list")
	public String EventList(ScheduleRequestParams params, Model model) {
		
		// 이벤트 리스트
		Map<String, Object> map = adminScheduleService.EventList(params);
		
		model.addAttribute("eventList",map);
		
		return "admin/schedule/list";
	}	
	
	@PostMapping("/save")
	public String EventSave(
			@RequestParam(name = "eno", required = false) Long eno,
			@RequestParam(name = "title") String title,
			@RequestParam(name = "startDate") String startDate,
			@RequestParam(name = "endDate") String endDate,
			@RequestParam(name = "highlightFlag") String highlightFlag,
			@RequestParam(name = "description") String description
			) {
		
				
		adminScheduleService.EventSave(eno, title, startDate, endDate, highlightFlag, description);
		
		return "redirect:/admin/schedule/list";  
	}	

	@PostMapping("/delete")
	public String EventDelete(@RequestParam(name="eno") Long eno) {
		
		System.out.println("ids : "+eno);
		
		adminScheduleService.EventDelete(eno);
		
		return "redirect:/admin/schedule/list";  
	}	
	
}
