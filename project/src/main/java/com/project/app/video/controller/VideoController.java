package com.project.app.video.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.video.dto.LikeRequest;
import com.project.app.video.dto.VideoDto;
import com.project.app.video.service.VideoService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/video")
@CrossOrigin(origins = "http://localhost:3000") // React 포트 허용
@RestController
@RequiredArgsConstructor
@Controller
public class VideoController {
	
	private final VideoService videoService;

	@ResponseBody
	@PostMapping("/togglelike")
	public Map<String, Object> toggleLike(@RequestBody LikeRequest dto) {
		
		Map<String, Object> map = videoService.toggleLike(dto);
		
		return map;
	}
	
	@ResponseBody
	@GetMapping("/getVideos")
	public List<VideoDto> getVideos(){
		
		List<VideoDto> list = videoService.findAll();
		
		return list;
	}
}
