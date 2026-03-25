package com.project.app.audition.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.TeamDto;

public interface TeamRepository extends JpaRepository<TeamDto, Long> {

}
