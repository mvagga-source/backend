package com.project.app.bookmark.repository;



import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.dto.PageType;
import com.project.app.bookmark.dto.ResponseBookmark;

public interface BookmarkRepository extends JpaRepository<BookmarkDto, Long> {

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
			    b.pageId,
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

	@Query(value="""
			select
			    b.id,
			    b.pageType,
			    b.createdAt,
			    b.pageId,
			    CASE 
			        WHEN b.pageType = 'VIDEO' THEN v.name
			        WHEN b.pageType = 'EVENT' THEN e.description
			        WHEN b.pageType = 'GOODS' THEN g.gname
			    END as name,
			    CASE 
			        WHEN b.pageType = 'VIDEO' THEN v.title
			        WHEN b.pageType = 'EVENT' THEN e.title
			        WHEN b.pageType = 'GOODS' THEN DBMS_LOB.SUBSTR(g.gcontent, 4000)
			    END as title
			FROM Bookmark b
			LEFT JOIN Video v 
			    ON b.pageId = v.id
			LEFT JOIN Event e 
			    ON b.pageId = e.eno
			LEFT JOIN Goods g 
			    ON b.pageId = g.gno    
			WHERE b.memberId = 'user001' 
			AND ( :pageType = 'ALL' OR b.pageType = :pageType )
			ORDER BY TO_CHAR(b.createdAt,'YYYY-MM-DD') DESC, b.pageType ASC
			""", nativeQuery = true)	
	List<Map<String, Object>> findByMemberId(
			@Param("memberId") String memberId,
			@Param("pageType") String pageType
	);
}
