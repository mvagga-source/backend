package com.project.app.audition.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.VoteBonusDto;

public interface VoteBonusRepository extends JpaRepository<VoteBonusDto, Long> {

}
