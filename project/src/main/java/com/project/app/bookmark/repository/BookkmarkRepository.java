package com.project.app.bookmark.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.dto.PageType;

public interface BookkmarkRepository extends JpaRepository<BookmarkDto, Long> {

	Optional<BookmarkDto> findByMemberIdAndPageIdAndPageType(
			String memberId, Long pageId, PageType pageType
	);

	@Query("SELECT b FROM BookmarkDto b WHERE b.memberId = :memberId and b.pageType = :pageType ")
	List<BookmarkDto> findByMemberIdAndPageType(
		@Param("memberId") String memberId, 
		@Param("pageType") PageType pageType
	);
}
