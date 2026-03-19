package com.project.app.calendar.repository;



import java.util.List;
import java.util.Optional;
import java.util.jar.Attributes.Name;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.calendar.dto.BookmarkDto;

public interface BookkmarkRepository extends JpaRepository<BookmarkDto, Long> {

	Optional<BookmarkDto> findByUserIdAndEvent_Eno(String userId, Long eno);

	@Query("SELECT b.event.eno FROM BookmarkDto b WHERE b.userId = :userId")
	List<Long> findEventIdsByUserId(@Param("userId") String userId);
}
