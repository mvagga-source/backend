package com.project.app.video.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.video.dto.LikeDto;

public interface LikeRepository extends JpaRepository<LikeDto, Long> {

	boolean existsByMember_IdAndVideo_Id(String memberId, Long videoId);

	void deleteByMember_IdAndVideo_Id(String memberId, Long videoId);
	
	int countByVideo_Id(Long videoId);

	List<LikeDto> findByMember_Id(String memberId);

	boolean existsByVideo_Id(Long id);


}
