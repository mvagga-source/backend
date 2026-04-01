package com.project.app.mypage.service;

import java.beans.Transient;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.audition.repository.VoteDetailRepository;
import com.project.app.audition.repository.VoteRepository;
import com.project.app.auth.dto.MemberDto;
import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.bookmark.repository.BookmarkRepository;
import com.project.app.common.Common;
import com.project.app.mypage.repository.MypageRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MypageServiceImpl implements MypageService {
	
	private final MypageRepository mypageRepository;
	private final BookmarkRepository bookkmarkRepository;
	private final VoteRepository voteRepository;
	private final VoteDetailRepository voteDetailRepository;
	private final HttpSession session;

	@Override
	public List<ResponseBookmark> findAll() {
		
//		List<BookmarkDto> list = bookkmarkRepository.findAll(); 
		List<ResponseBookmark> list = bookkmarkRepository.findBookmarks();
		
		return list;
	}

	@Override
	public void deleteBookmarkById(Long id) {
		
		bookkmarkRepository.deleteById(id);
	}

	@Override
	public List<Map<String, Object>> findById(int page, int size, String startDate, String endDate) {
		
		MemberDto memberDto = Common.idCheck(session);
		
		List<Map<String, Object>> list  = mypageRepository.findIdolsById(memberDto.getId(), startDate, endDate);
		
		return list;  
	}

	@Transactional
	@Override
	public void deleteVoteById(Long id) {
		
		voteDetailRepository.deleteById(id);
		voteRepository.deleteById(id);
		
	}

}
