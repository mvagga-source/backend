package com.project.app.schedule.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.app.schedule.dto.EventDto;

public interface EventService {

	List<EventDto> findAll();

}
