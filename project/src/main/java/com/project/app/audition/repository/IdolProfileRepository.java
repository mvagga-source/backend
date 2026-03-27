package com.project.app.audition.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.IdolProfileDto;

public interface IdolProfileRepository extends JpaRepository<IdolProfileDto, Long>{

}
