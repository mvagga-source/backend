package com.project.app.mypage.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.project.app.audition.dto.VoteDto;
import com.project.app.audition.repository.VoteRepository;
import com.project.app.auth.dto.MemberDto;
import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.bookmark.repository.BookmarkRepository;
import com.project.app.common.Common;
import com.project.app.mypage.dto.MyVoteResponse;
import com.project.app.mypage.repository.MypageRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MypageServiceImpl implements MypageService {
	
	private final MypageRepository mypageRepository;
	private final BookmarkRepository bookkmarkRepository;
	private final VoteRepository voteRepository;
	private final HttpSession session;

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

	@Override
	public List<Map<String, Object>> findById(int page, int size) {
		
		MemberDto memberDto = Common.idCheck(session);
		
		List<Map<String, Object>> list  = mypageRepository.findIdolsById(memberDto.getId());
		
		return list;   
	}

}
