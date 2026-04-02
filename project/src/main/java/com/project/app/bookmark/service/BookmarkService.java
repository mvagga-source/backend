package com.project.app.bookmark.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.app.bookmark.dto.BookmarkRequest;
import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.bookmark.dto.BookmarkDto;

public interface BookmarkService {
	
	// 북마크 토글
	boolean toggleBookmark(BookmarkRequest dto);
}
