package com.project.app.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.video.dto.VideoDto;

public interface AdminVideoRepository extends JpaRepository<VideoDto, Long> {

	
	
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
	          )
	        )			
		"""
	,nativeQuery = true)
	Page<VideoDto> findVideoList(
			@Param("search") String search, 
			@Param("searchType") String searchType, 
			Pageable pageable);

}
