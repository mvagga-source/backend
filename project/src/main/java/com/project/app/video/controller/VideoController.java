package com.project.app.video.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.common.AjaxResponse;
import com.project.app.video.dto.AVideoRequestParams;
import com.project.app.video.dto.IdListDto;
import com.project.app.video.dto.LikeDto;
import com.project.app.video.dto.LikeRequest;
import com.project.app.video.dto.VideoDto;
import com.project.app.video.dto.VideoRequestParams;
import com.project.app.video.service.VideoService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/video")
@RequiredArgsConstructor
@RestController
@Controller
public class VideoController {
	
	private final VideoService videoService;

	@GetMapping("/getVideoPage")
	public AjaxResponse getVideoPage(VideoRequestParams params){
		
		Map<String, Object> list = videoService.findVideoPage(params);
		
		return AjaxResponse.success(list);
	}
	
	@GetMapping("/getVideo")
	public VideoDto getVideo(@RequestParam(name = "pageId", required = false) Long pageId){
		
		System.out.println("pageId : "+pageId);
		
		VideoDto videoDto = videoService.findById(pageId);
		
		return videoDto;
	}	
	
	@GetMapping("/getMyLikes")
	public List<LikeDto> getMyLikes(@RequestParam(name = "memberId", required = false) String memberId) {
		
		List<LikeDto> list = videoService.findMyLikes(memberId);
		
		return list;
	}

	
	@PostMapping("/toggleVideoLike")
	public Map<String, Object> toggleVideoLike(@RequestBody LikeRequest dto) {
		
		Map<String, Object> map = videoService.toggleVideoLike(dto);
		
		return map;
	}
	
	@PostMapping("/videoViewCount")
	public void videoViewCount(@RequestBody LikeRequest dto) {
		videoService.videoViewCount(dto.getVideoId());
	}

	@PostMapping("/saveVideo")
	public VideoDto saveVideo(@RequestBody AVideoRequestParams params) {
		
		VideoDto videoDto = videoService.saveVideo(params);
		return videoDto;
	}
	
	@DeleteMapping("/deleteVideo")
	public void deleteVideo(@RequestBody IdListDto dto ) {
		
		System.out.println("ids : "+dto.getIds());
		
		videoService.deleteVideos(dto.getIds());
	}	
	
	@GetMapping("/getIdolStatus")
	public AjaxResponse getIdolStatus(){
		
		System.out.println("== getIdolStatus =="); 
		
		List<Map<String, Object>> list = videoService.findIdolStatus();
		
		return AjaxResponse.success(list);
	}
	
	
}
