package com.project.app.calendar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.app.calendar.dto.Bookmark;
import com.project.app.calendar.dto.BookmarkDto;
import com.project.app.calendar.dto.EventDto;
import com.project.app.calendar.repository.BookkmarkRepository;
import com.project.app.calendar.repository.EventRepository;

@Service
public class BookmarkServiceImpl implements BookmarkService {
	
	@Autowired BookkmarkRepository bookkmarkRepository;
	@Autowired EventRepository eventRepository;	

	@Override
	public List<Long> findEventIdsByUserId(String userId) {
		
		List<Long> list = bookkmarkRepository.findEventIdsByUserId(userId);
		
		return list;
	}

	@Override
	public boolean toggleBookmark(Bookmark dto) {
		
		Optional<BookmarkDto> bookmarkDto =
				bookkmarkRepository.findByUserIdAndEvent_Eno(dto.getUserId(), dto.getEno());

	        if(bookmarkDto.isPresent()) {
	        	bookkmarkRepository.delete(bookmarkDto.get());
	            return false;
	        }

	        EventDto eventDto = eventRepository.findById(dto.getEno()).orElseThrow();

	        BookmarkDto bDto = new BookmarkDto();
	        bDto.setUserId(dto.getUserId());
	        bDto.setEvent(eventDto);
	        bDto.setPageType(dto.getPageType());

	        bookkmarkRepository.save(bDto);

	        return true;
	}

	@Override
	public Page<BookmarkDto> findAll(Pageable pageable) {
		
		Page<BookmarkDto> list = bookkmarkRepository.findAll(pageable);
		
		return list;
	}

	@Override
	public void deleteById(Long id) {
		
		bookkmarkRepository.deleteById(id);

	}

}
