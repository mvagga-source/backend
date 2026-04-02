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
}
