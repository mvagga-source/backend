package com.project.app.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.calendar.dto.EventDto;

public interface EventRepository extends JpaRepository<EventDto, Long> {

}
