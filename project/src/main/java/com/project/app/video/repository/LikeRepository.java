package com.project.app.video.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.video.dto.LikeDto;

public interface LikeRepository extends JpaRepository<LikeDto, Long> {

	boolean existsByMember_IdAndVideo_Id(String memberId, Long videoId);

	void deleteByMember_IdAndVideo_Id(String memberId, Long videoId);
	
	int countByVideo_Id(Long videoId);


}
