package com.project.app.admin.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.app.admin.repository.AdminVideoRepository;
import com.project.app.video.dto.VideoDto;
import com.project.app.video.dto.VideoRequestParams;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminVideoServiceImpl implements AdminVideoService {

	private final AdminVideoRepository adminVideoRepository;
	
	@Transactional
	@Override
	public void save(Long id) {
		
		VideoDto videoDto = adminVideoRepository.findById(id).orElse(null);
		videoDto.setDeletedFlag("Y");
		videoDto.setDeletedAt(LocalDateTime.now());

		adminVideoRepository.save(videoDto);
	}

	@Override
	public Map<String, Object> findVideoList(VideoRequestParams params) {

		Sort sort;
		Pageable pageable;
		Page<VideoDto> pageList = null; 
	
		switch (params.getSortType()){
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
		
		pageable = PageRequest.of(params.getPage()-1, params.getSize(), sort);
		
		pageList = adminVideoRepository.findVideoList(params.getSearch(),params.getSearchType(),pageable);
		
		Map<String, Object> map = new HashMap<>();
		map.put("list", pageList.getContent());
        map.put("page", params.getPage());
        map.put("size", params.getSize());
        map.put("search", params.getSearch());
        map.put("searchType", params.getSearchType());
        map.put("maxPage", pageList.getTotalPages());
        
        int startPage = ((params.getPage() - 1) /params.getSize()) * params.getSize()  + 1;
        int endPage = startPage + params.getSize() - 1;
        if (endPage > pageList.getTotalPages()) endPage = pageList.getTotalPages();
        
        map.put("startPage", startPage);        
        map.put("endPage", endPage);                
        map.put("totalCount", pageList.getTotalElements());		

		return map;		
	}

}
