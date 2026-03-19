package com.project.app.calendar.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.app.calendar.dto.EventDto;

public interface EventService {

	List<EventDto> findAll();

	Page<EventDto> findAll(Pageable pageable);

}
