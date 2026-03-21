package com.project.app.video.service;

import java.util.List;
import java.util.Map;

import com.project.app.video.dto.LikeRequest;
import com.project.app.video.dto.VideoDto;

public interface VideoService {

	// 좋아요 카운트 또는 취소
	Map<String, Object> toggleLike(LikeRequest dto);

	// 비디오 전체 리스트
	List<VideoDto> findAll();

}
