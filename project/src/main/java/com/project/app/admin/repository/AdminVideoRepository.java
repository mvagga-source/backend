package com.project.app.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.video.dto.VideoDto;

public interface AdminVideoRepository extends JpaRepository<VideoDto, Long> {

}
