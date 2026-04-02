package com.project.app.bookmark.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.dto.BookmarkRequest;
import com.project.app.bookmark.dto.PageType;
import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.bookmark.service.BookmarkService;
import com.project.app.common.AjaxResponse;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/bookmark")
@RestController
@RequiredArgsConstructor
@Controller
public class BookmarkController {
	
	private final BookmarkService bookmarkService;


	// 북마크 토글
	@PostMapping("/toggleBookmark")
    public ResponseEntity<Boolean> toggleBookmark(@RequestBody BookmarkRequest dto)
	{
		System.out.println("dto : "+dto.getMemberId());
		
        return ResponseEntity.ok(bookmarkService.toggleBookmark(dto));
    }
	
}
