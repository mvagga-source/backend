package com.project.app.video.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.video.dto.IdListDto;
import com.project.app.video.dto.LikeDto;
import com.project.app.video.dto.LikeRequest;
import com.project.app.video.dto.VideoDto;
import com.project.app.video.service.VideoService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/video")
@RequiredArgsConstructor
@RestController
@Controller
public class VideoController {
	
	private final VideoService videoService;

	@GetMapping("/getVideos")
	public Page<VideoDto> getVideos(
			@RequestParam(name = "page", defaultValue = "1") int page,
	        @RequestParam(name = "size", defaultValue = "10") int size,
			@RequestParam(name = "sortType", defaultValue = "LATEST") String sortType,
			@RequestParam(name = "search", required = false) String search,
			@RequestParam(name = "searchType", required = false) String searchType
			
			){
		
//		List<VideoDto> list = videoService.findAll();
		Page<VideoDto> list = videoService.getVideos(page, size, sortType, search, searchType);
		
		return list;
	}
	
	@GetMapping("/getVideo")
	public VideoDto getVideo(@RequestParam(name = "pageId", required = false) Long pageId){
		
		System.out.println("pageId : "+pageId);
		
		VideoDto videoDto = videoService.findById(pageId);
		
		return videoDto;
	}	
	
	@GetMapping("/getMyLikes")
	public List<LikeDto> getMyLikes(@RequestParam("memberId") String memberId) {
		
		List<LikeDto> list = videoService.findByMember_Id(memberId);
		
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
	public VideoDto saveVideo(@RequestBody VideoDto dto) {
		
		VideoDto videoDto = videoService.saveVideo(dto);
		return videoDto;
	}
	
	@DeleteMapping("/deleteVideo")
	public void deleteVideo(@RequestBody IdListDto dto ) {
		
		System.out.println("ids : "+dto.getIds());
		
		videoService.deleteVideos(dto.getIds());
	}	
	
	
}
