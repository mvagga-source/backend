package com.project.app.bookmark.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.app.bookmark.dto.BookmarkRequest;
import com.project.app.bookmark.dto.BookmarkDto;

public interface BookmarkService {

	// 전체 북마크 가져오기
	Page<BookmarkDto> findAll(Pageable pageable);	
	
	// 내 북마크 정보 가져오기
	List<BookmarkDto> findByMemberIdAndPageType(BookmarkRequest dto);

	// 북마크 토글
	boolean toggleBookmark(BookmarkRequest dto);


}
