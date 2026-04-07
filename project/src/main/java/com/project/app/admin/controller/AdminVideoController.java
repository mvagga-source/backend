package com.project.app.admin.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.admin.service.AdminNoticeService;
import com.project.app.admin.service.AdminVideoService;
import com.project.app.audition.dto.IdolProfileDto;
import com.project.app.audition.service.IdolProfileService;
import com.project.app.common.AjaxResponse;
import com.project.app.video.dto.AVideoRequestParams;
import com.project.app.video.dto.IdListDto;
import com.project.app.video.dto.VideoDto;
import com.project.app.video.dto.VideoRequestParams;
import com.project.app.video.service.VideoService;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/video")
@RequiredArgsConstructor
public class AdminVideoController {
	
	private final VideoService videoService;
	private final IdolProfileService idolProfileService;
	private final AdminVideoService adminVideoService;

	@GetMapping("/list")
	public String listJoinIdol(VideoRequestParams params, Model model) {
		

		// 아이돌 리스트(진출자만)
		List<IdolProfileDto> list = idolProfileService.findAll();
		
		// 비디오 리스트
		Map<String, Object> map = adminVideoService.findVideoList(params);
		
		model.addAttribute("videoList",map);
		model.addAttribute("idolList",list);
		
		return "admin/video/list";
	}	
	
	
	@PostMapping("/saveVideo")
	public String saveVideo(AVideoRequestParams params) {
		
		VideoDto videoDto = videoService.saveVideo(params);
		
		return "redirect:/admin/video/list";
	}	

	@PostMapping("/delete")
	public String deleteVideo(@RequestParam(name="id") Long id) {
		
		adminVideoService.save(id);
		
		return "admin/video/list";
	}	
	
}
