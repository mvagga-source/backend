package com.project.app.schedule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.schedule.dto.EventDto;

public interface EventRepository extends JpaRepository<EventDto, Long> {

	List<EventDto> findAllByDeletedFlag(String string);

}
