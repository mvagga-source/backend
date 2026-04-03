package com.project.app.audition.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.SupportProjectDto;

import org.springframework.stereotype.Repository;

@Repository
public interface SupportProjectRepository extends JpaRepository<SupportProjectDto, Long> {
    // 이제 findById(Long) 메서드가 정상적으로 상속됩니다.
}