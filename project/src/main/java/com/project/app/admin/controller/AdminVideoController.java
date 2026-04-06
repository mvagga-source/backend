package com.project.app.admin.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.app.admin.service.AdminNoticeService;
import com.project.app.audition.dto.IdolProfileDto;
import com.project.app.audition.service.IdolProfileService;
import com.project.app.common.AjaxResponse;
import com.project.app.video.dto.AVideoRequestParams;
import com.project.app.video.dto.VideoDto;
import com.project.app.video.dto.VideoRequestParams;
import com.project.app.video.service.VideoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/video")
@RequiredArgsConstructor
public class AdminVideoController {
	
	private final VideoService videoService;
	private final IdolProfileService idolProfileService;

	@GetMapping("/list")
	public String list(VideoRequestParams params, Model model) {
		
		System.out.println("size : "+params.getSize());
		
		// 아이돌 리스트(진출자만)
		List<IdolProfileDto> list = idolProfileService.findAll();
		
		// 비디오 리스트
		Map<String, Object> map = videoService.findVideoPage(params);
		
//		System.out.println("page list : "+ ((List)map.get("list")).size());
//		System.out.println("page : "+map.get("page"));
		
		model.addAttribute("videoList",map);
		model.addAttribute("idolList",list);
		
		return "admin/video/list";
	}
	
	@PostMapping("/saveVideo")
	public String saveVideo(AVideoRequestParams params) {
		
		VideoDto videoDto = videoService.saveVideo(params);
		
		return "redirect:/admin/video/list";
	}	
	
}
