package com.project.app.video.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@Transactional
	@Modifying
	@Query("update VideoDto v set v.viewCount = v.viewCount + 1 where v.id = :videoId")
	void videoViewCount(@Param("videoId") Long videoId);

//	@Query("""
//			SELECT v FROM VideoDto v
//			ORDER BY 
//			CASE 
//				WHEN :sortType = 'LATEST' THEN v.createdAt
//			    WHEN :sortType = 'LIKE' THEN v.likeCount
//			    WHEN :sortType = 'VIEW' THEN v.viewCount
//			    ELSE (v.likeCount * 2 + v.viewCount)
//			END DESC
//			""")
//	List<VideoDto> findVideos(@Param("sortType") String sortType);

//	List<VideoDto> findByOrderByCreatedAtDesc();
//
//	List<VideoDto> findByOrderByLikeCountDesc();
//
//	List<VideoDto> findByOrderByViewCountDesc();


	@Query(value = """
			SELECT *
			FROM video v
			ORDER BY
			(v.likecount * 2 + v.viewcount)
			/ ((SYSDATE - TRUNC(v.createdAt)) + 1) DESC
			FETCH FIRST :#{#pageable.pageSize} ROWS ONLY
			""", nativeQuery = true)
	Page<VideoDto> findPopularVideos(Pageable pageable);

	Page<VideoDto> findAll(Pageable pageable);

}
