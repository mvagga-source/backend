package com.project.app.bookmark.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.dto.PageType;
import com.project.app.bookmark.dto.ResponseBookmark;

public interface BookkmarkRepository extends JpaRepository<BookmarkDto, Long> {

	Optional<BookmarkDto> findByMemberIdAndPageIdAndPageType(
			String memberId, Long pageId, PageType pageType
	);

	@Query("SELECT b FROM BookmarkDto b WHERE b.memberId = :memberId and b.pageType = :pageType ")
	List<BookmarkDto> findByMemberIdAndPageType(
		@Param("memberId") String memberId, 
		@Param("pageType") PageType pageType
	);
	

	@Query(value = """
			SELECT
			    b.id,
			    b.pageType,
			    b.createdAt,
			    CASE 
			        WHEN b.pageType = 'VIDEO' THEN v.name
			        WHEN b.pageType = 'EVENT' THEN e.description
			    END as name,
			    CASE 
			        WHEN b.pageType = 'VIDEO' THEN v.title
			        WHEN b.pageType = 'EVENT' THEN e.title
			    END as title
			FROM Bookmark b
			LEFT JOIN Video v 
			    ON b.pageType = 'VIDEO' AND b.pageId = v.id
			LEFT JOIN Event e 
			    ON b.pageType = 'EVENT' AND b.pageId = e.eno
			ORDER BY TO_CHAR(b.createdAt,'YYYY-MM-DD') DESC, b.pageType ASC
			""", nativeQuery = true)
	List<ResponseBookmark> findBookmarks();
}
