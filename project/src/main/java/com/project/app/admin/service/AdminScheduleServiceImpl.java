package com.project.app.admin.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.app.admin.dto.schedule.ScheduleRequestParams;
import com.project.app.admin.repository.AdminScheduleRepository;
import com.project.app.admin.repository.AdminVideoRepository;
import com.project.app.schedule.dto.EventDto;
import com.project.app.video.dto.VideoDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminScheduleServiceImpl implements AdminScheduleService {
	
	private final AdminScheduleRepository adminScheduleRepository;

	@Override
	public Map<String, Object> EventList(ScheduleRequestParams params) {
		
		Sort sort;
		Pageable pageable;
		Page<EventDto> pageList = null; 
	
       	sort = Sort.by("createdAt").descending().and(Sort.by("eno").descending());

		pageable = PageRequest.of(params.getPage()-1, params.getSize(), sort);
		
		pageList = adminScheduleRepository.findEventList(params.getSearch(),params.getSearchType(),params.getStartDate(),params.getEndDate(), pageable);
		
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

	@Transactional
	@Override
	public void EventSave(Long eno, String title, String startDate, String endDate, String highlightFlag, String description) {
		
		EventDto eventDto;
		
		if (eno != null) {
			// 수정
			eventDto = adminScheduleRepository.findByEno(eno).orElse(null);
		}else {
			// 저장
			eventDto = new EventDto();
		}
		 
		eventDto.setTitle(title);
		eventDto.setDescription(description);
		eventDto.setStartDate(startDate);
		eventDto.setEndDate(endDate);
		eventDto.setHighlightFlag(highlightFlag);
		
		adminScheduleRepository.save(eventDto);
	}

	@Transactional
	@Override
	public void EventDelete(Long eno) {
		
		EventDto eventDto = adminScheduleRepository.findByEno(eno).orElse(null);
		if (eventDto != null) {
			eventDto.setDeletedFlag("Y");
			eventDto.setDeletedAt(LocalDateTime.now());
			adminScheduleRepository.save(eventDto);
		}
	}

}
