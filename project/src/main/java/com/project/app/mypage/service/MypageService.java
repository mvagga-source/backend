package com.project.app.mypage.service;

import java.util.List;

import com.project.app.bookmark.dto.ResponseBookmark;

public interface MypageService {

	// 북마크 전체 정보
	List<ResponseBookmark> findAll();

	void deleteById(Long id);

}
