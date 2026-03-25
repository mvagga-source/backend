package com.project.app.audition.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.TeamMatchDto;

public interface TeamMatchRepository extends JpaRepository<TeamMatchDto, Long> {

}
