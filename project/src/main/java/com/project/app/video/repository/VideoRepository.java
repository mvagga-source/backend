package com.project.app.video.repository;

import java.util.List;
import java.util.Map;

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


	@Transactional
	@Modifying
	@Query("update VideoDto v set v.popCount = v.popCount + :score where v.id = :videoId")
	void videoPopCount(@Param("videoId") Long videoId, @Param("score") double score);

	Page<VideoDto> findAllByDeletedFlag(String string, Pageable pageable);

//	Page<VideoDto> findByDeletedFlagAndNameContainingOrDeletedFlagAndTitleContaining(String string, String search,
//			String string2, String search2, Pageable pageable);

//	Page<VideoDto> findByDeletedFlagAndNameContaining(String string, String search, Pageable pageable);

	Page<VideoDto> findByDeletedFlagAndTitleContaining(String string, String search, Pageable pageable);

	List<VideoDto> findAllByDeletedFlag(String string);

	     
	@Query(value="""
			select idol_profile_id, status from (
			select 
			    audition_id, idol_profile_id, status,
			    ROW_NUMBER() OVER (PARTITION BY idol_profile_id order by audition_id desc) as au
			from idol
			) where au = 1 and status = 'active'
			""",nativeQuery = true)
	List<Map<String, Object>> findIdolStatus();
	
	
	@Query(value="""
		    SELECT v.* 
		    FROM video v
		    LEFT JOIN idol_profile i
		         ON v.profileId = i.profileId
		    WHERE 
		        (:search IS NULL OR :search = ''
		          OR (
		                 (:searchType = 'TITLE' AND v.title LIKE '%' || :search || '%') 
		              OR (:searchType = 'NAME' AND i.name LIKE '%' || :search || '%')
		              OR (:searchType = 'ALL' AND i.name LIKE '%' || :search || '%' OR v.title LIKE '%' || :search || '%')
		          )
		        )
		""",
		countQuery = """
		    SELECT count(*) 
		    FROM video v
		    LEFT JOIN idol_profile i
		         ON v.profileId = i.profileId
		    WHERE 
		        (:search IS NULL OR :search = ''
		          OR (
		                 (:searchType = 'TITLE' AND v.title LIKE '%' || :search || '%') 
		              OR (:searchType = 'NAME' AND i.name LIKE '%' || :search || '%')
		              OR (:searchType = 'ALL' AND i.name LIKE '%' || :search || '%' AND v.title LIKE '%' || :search || '%')		              
		          )
		        )			
			"""
		,nativeQuery = true)	
	Page<VideoDto> findVideoList(
			@Param("search") String search, 
			@Param("searchType") String searchType, 
			Pageable pageable
	);





}
