package com.project.app.calendar.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.app.calendar.dto.Bookmark;
import com.project.app.calendar.dto.BookmarkDto;

public interface BookmarkService {

	// 
	List<Long> findEventIdsByUserId(String string);

	boolean toggleBookmark(Bookmark dto);

	Page<BookmarkDto> findAll(Pageable pageable);

	void deleteById(Long id);

}
