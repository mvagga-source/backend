package com.project.app.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.bookmark.repository.BookmarkRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MypageServiceImpl implements MypageService {
	
	private final BookmarkRepository bookkmarkRepository;

	@Override
	public List<ResponseBookmark> findAll() {
		
//		List<BookmarkDto> list = bookkmarkRepository.findAll(); 
		List<ResponseBookmark> list = bookkmarkRepository.findBookmarks();
		
		return list;
	}

	@Override
	public void deleteById(Long id) {
		
		bookkmarkRepository.deleteById(id);
	}

}
