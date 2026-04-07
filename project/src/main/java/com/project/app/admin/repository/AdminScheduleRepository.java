package com.project.app.admin.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.schedule.dto.EventDto;
import com.project.app.video.dto.VideoDto;

public interface AdminScheduleRepository extends JpaRepository<EventDto, Long> {

	@Query(value="""
		    SELECT * 
		    FROM event
		    WHERE 
		        (:search IS NULL OR :search = ''
		        OR (
		                 (:searchType = 'TITLE' AND title LIKE '%' || :search || '%') 
		              OR (:searchType = 'CONTENT' AND description LIKE '%' || :search || '%')
		          )
		        )
		        AND (:startDate IS NULL OR createdAt >= TO_DATE(:startDate,'YYYY-MM-DD'))
				     AND (:endDate IS NULL OR createdAt < TO_DATE(:endDate,'YYYY-MM-DD') + 1)
		"""
		,nativeQuery = true)	
	Page<EventDto> findEventList(
			@Param("search") String search, 
			@Param("searchType") String searchType,
			@Param("startDate") String startDate, 
			@Param("endDate") String endDate, 
			Pageable pageable);

	Optional<EventDto> findByEno(Long eno);

}
