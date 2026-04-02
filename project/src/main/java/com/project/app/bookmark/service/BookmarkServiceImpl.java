package com.project.app.bookmark.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.dto.BookmarkRequest;
import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.bookmark.repository.BookmarkRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BookmarkServiceImpl implements BookmarkService {
	
	private final BookmarkRepository bookmarkRepository;

	// 전체 북마크 가져오기
	@Override
	public Page<BookmarkDto> findAll(Pageable pageable) {
		
		Page<BookmarkDto> list = bookmarkRepository.findAll(pageable);
		
		return list;
	}	
	
	// 내 북마크 가져오기
	@Override
	public List<BookmarkDto> findByMemberIdAndPageType(BookmarkRequest dto) {
		
		List<BookmarkDto> list = bookmarkRepository.findByMemberIdAndPageType(
				dto.getMemberId(),dto.getPageType()
		);
		
		return list;
	}	
	
	
	// 북마크 추가 또는 삭제
	@Override
	public boolean toggleBookmark(BookmarkRequest dto) {
		
		Optional<BookmarkDto> bookmarkDto =
				bookmarkRepository.findByMemberIdAndPageIdAndPageType(
						dto.getMemberId(),dto.getPageId(),dto.getPageType()
				);

        if(bookmarkDto.isPresent()) {
        	bookmarkRepository.delete(bookmarkDto.get());
            return false;
        }

        BookmarkDto bookmarkDto2 = new BookmarkDto();
        
        bookmarkDto2.setMemberId(dto.getMemberId());
        bookmarkDto2.setPageId(dto.getPageId());
        bookmarkDto2.setPageType(dto.getPageType());
        
        bookmarkRepository.save(bookmarkDto2);

        return true;
	}

	@Override
	public List<Map<String, Object>> findByMemberId(String memberId, String pageType,String startDate, String endDate) {
		
		List<Map<String, Object>> list = bookmarkRepository.findByMemberId(memberId, pageType, startDate, endDate);

		return list;
	}
	
}
