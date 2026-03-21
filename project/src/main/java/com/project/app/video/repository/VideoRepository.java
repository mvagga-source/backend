package com.project.app.video.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.video.dto.VideoDto;

import jakarta.transaction.Transactional;

public interface VideoRepository extends JpaRepository<VideoDto, Long> {

	@Transactional
	@Modifying
	@Query("update VideoDto v set v.likeCount = v.likeCount + 1 where v.id = :videoId")
	void increaseLikeCount(@Param("videoId") Long videoId);
	
	@Transactional
	@Modifying
	@Query("update VideoDto v set v.likeCount = v.likeCount - 1 where v.id = :videoId")
	void decreaseLikeCount(@Param("videoId") Long videoId);

}
