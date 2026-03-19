package com.project.app.calendar.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.app.calendar.dto.EventDto;
import com.project.app.calendar.repository.EventRepository;

@Service
public class EventServiceImpl implements EventService {
	
	@Autowired EventRepository eventRepository;

	@Override
	public List<EventDto> findAll() {
		
		List<EventDto> list = eventRepository.findAll();
		
		return list;
	}

	@Override
	public Page<EventDto> findAll(Pageable pageable) {
		
		Page<EventDto> list = eventRepository.findAll(pageable);
		
		return list;
	}

}
