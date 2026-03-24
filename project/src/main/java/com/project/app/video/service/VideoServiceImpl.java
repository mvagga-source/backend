package com.project.app.video.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.repository.MemberRepository;
import com.project.app.video.dto.LikeDto;
import com.project.app.video.dto.LikeRequest;
import com.project.app.video.dto.VideoDto;
import com.project.app.video.repository.LikeRepository;
import com.project.app.video.repository.VideoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VideoServiceImpl implements VideoService {

	private final LikeRepository likeRepository;
	private final VideoRepository videoRepository;
	private final MemberRepository memberRepository;

	@Transactional
	@Override
	public Map<String, Object> toggleVideoLike(LikeRequest dto) {
		
		boolean exists = likeRepository.existsByMember_IdAndVideo_Id(dto.getMemberId(), dto.getVideoId());
		
		if (exists) {

			likeRepository.deleteByMember_IdAndVideo_Id(dto.getMemberId(), dto.getVideoId());
			videoRepository.decreaseLikeCount(dto.getVideoId());

	    } else {
	    	
			 MemberDto memberDto = memberRepository.findById(dto.getMemberId()).orElse(null);
			 VideoDto videoDto = videoRepository.findById(dto.getVideoId()).orElse(null);


	    	LikeDto likeDto = new LikeDto();

	    	likeDto.setMember(memberDto);
	    	likeDto.setVideo(videoDto);
	    	
	        likeRepository.save(likeDto);
	        
	        // 좋아요수 증가
	        videoRepository.increaseLikeCount(dto.getVideoId());
	        
	        // 인기순 계산
			double hours = Duration.between(videoDto.getCreatedAt(), LocalDateTime.now())
			        .toMinutes() / (1000.0 * 60 * 60);

	        double score = ((videoDto.getLikeCount()+1) * 2 + videoDto.getViewCount()) - (hours * 0.1);
//	        long score = ((videoDto.getLikeCount()+1) * 2 + videoDto.getViewCount());
			
			System.out.println(score);
			
			videoRepository.videoPopCount(dto.getVideoId(), score);	        
	        
	    }

	    int likeCount = likeRepository.countByVideo_Id(dto.getVideoId());

	    return Map.of(
	        "liked", !exists,
	        "likeCount", likeCount
	    );
	}


	@Override
	public List<VideoDto> findAll() {
		
		List<VideoDto> list = videoRepository.findAll();
		
		return list;
	}


	@Override
	public void videoViewCount(Long videoId) {

		// 조회수 증가
		videoRepository.videoViewCount(videoId);

        // 인기순 계산
		VideoDto videoDto = videoRepository.findById(videoId).orElse(null);
		
		double hours = Duration.between(videoDto.getCreatedAt(), LocalDateTime.now())
		        .toMinutes() / (1000.0 * 60 * 60);

        double score = (videoDto.getLikeCount() * 2 + (videoDto.getViewCount()+1)) - (hours * 0.1);
//        long score = (videoDto.getLikeCount() * 2 + (videoDto.getViewCount()+1));
        
		videoRepository.videoPopCount(videoId, score);
	}


	@Override
	public List<LikeDto> findByMember_Id(String memberId) {
		
		List<LikeDto> list = likeRepository.findByMember_Id(memberId);
		
		return list;
	}


	@Override
	public Page<VideoDto> getVideos(int page, int size, String sortType, String search, String searchType) {
		
		Sort sort;
		Pageable pageable;
		Page<VideoDto> pageList = null; 
	
		switch (sortType) {
	        case "POPULAR":
	        	sort = Sort.by("popCount").descending().and(Sort.by("id").descending());
	            break;
	            
	        case "LIKE":
	        	sort = Sort.by("likeCount").descending().and(Sort.by("id").descending());
	            break;
	            
	        case "VIEW":
	        	sort = Sort.by("viewCount").descending().and(Sort.by("id").descending());
	            break;
	            
	        default:
	        	sort = Sort.by("createdAt").descending().and(Sort.by("id").descending());
	        	break;
		}
		
		pageable = PageRequest.of(page, size, sort);
		
		if (search == null || search.trim().isEmpty()) {
	        pageList = videoRepository.findAll(pageable);
	    }else if(searchType.equals("ALL")) {
	    	pageList = videoRepository.findByNameContainingOrTitleContaining(search, search, pageable);
	    }else if(searchType.equals("NAME")) {
	    	pageList = videoRepository.findByNameContaining(search,pageable);
	    }else if(searchType.equals("TITLE")) {
	    	pageList = videoRepository.findByTitleContaining(search,pageable);
	    }

		return pageList;
	}




}
