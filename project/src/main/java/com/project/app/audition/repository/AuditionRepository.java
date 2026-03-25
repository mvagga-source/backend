package com.project.app.audition.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.AuditionDto;

public interface AuditionRepository extends JpaRepository<AuditionDto, Long> {

}
