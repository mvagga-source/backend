package com.project.app.schedule.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.app.schedule.dto.EventDto;
import com.project.app.schedule.repository.EventRepository;
import com.project.app.video.dto.VideoDto;

import jakarta.transaction.Transactional;

@Service
public class EventServiceImpl implements EventService {
	
	@Autowired EventRepository eventRepository;

	@Override
	public List<EventDto> findAll() {
		
		List<EventDto> list = eventRepository.findAllByDeletedFlag("N");
		
		return list;
	}

	@Transactional	
	@Override
	public EventDto save(EventDto dto) {
		
		EventDto eventDto;
		
		if (dto.getEno() != null) {
			// 수정
			eventDto = eventRepository.findById(dto.getEno()).orElse(null);
		}else {
			// 저장
			eventDto = new EventDto();
		}
		 
		eventDto.setTitle(dto.getTitle());
		eventDto.setDescription(dto.getDescription());
		eventDto.setStartDate(dto.getStartDate());
		eventDto.setEndDate(dto.getEndDate());

		return eventRepository.save(eventDto); 
	}

	@Override
	public void deleteEvents(List<Long> ids) {

		eventRepository.findAllById(ids).forEach(event -> {
			event.setDeletedFlag("Y");
			event.setDeletedAt(LocalDateTime.now());
		});
		
		eventRepository.saveAll(eventRepository.findAllById(ids));		
	}
}
