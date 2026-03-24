package com.project.app.video.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.project.app.video.dto.LikeDto;
import com.project.app.video.dto.LikeRequest;
import com.project.app.video.dto.VideoDto;

public interface VideoService {

	// 좋아요 카운트 또는 취소
	Map<String, Object> toggleVideoLike(LikeRequest dto);

	// 비디오 전체 리스트
	List<VideoDto> findAll();

	// 조회수 카운트
	void videoViewCount(Long videoId);

	// 나의 좋아요 리스트
	List<LikeDto> findByMember_Id(String memberId);

	// 검색
	Page<VideoDto> getVideos(int page, int size, String sortType, String search, String searchType);
	
	// 비디오 한건
	VideoDto findById(Long videoId);

}
