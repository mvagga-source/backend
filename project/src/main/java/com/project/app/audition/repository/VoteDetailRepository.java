package com.project.app.audition.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.VoteDetailDto;

public interface VoteDetailRepository extends JpaRepository<VoteDetailDto, Long> {

}
