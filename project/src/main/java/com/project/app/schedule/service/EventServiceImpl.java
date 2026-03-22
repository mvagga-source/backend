package com.project.app.schedule.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.app.schedule.dto.EventDto;
import com.project.app.schedule.repository.EventRepository;

@Service
public class EventServiceImpl implements EventService {
	
	@Autowired EventRepository eventRepository;

	@Override
	public List<EventDto> findAll() {
		
		List<EventDto> list = eventRepository.findAll();
		
		return list;
	}
}
