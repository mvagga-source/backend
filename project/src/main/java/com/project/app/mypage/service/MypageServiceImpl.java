package com.project.app.mypage.service;

import java.beans.Transient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.audition.repository.VoteDetailRepository;
import com.project.app.audition.repository.VoteRepository;
import com.project.app.auth.dto.MemberDto;
import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.dto.BookmarkRequest;
import com.project.app.bookmark.dto.ResponseBookmark;
import com.project.app.bookmark.repository.BookmarkRepository;
import com.project.app.common.Common;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;
import com.project.app.goodsorders.repository.GoodsOrdersRepository;
import com.project.app.mypage.dto.MyRequestParams;
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
	private final GoodsOrdersRepository goodsOrdersRepository;
	private final HttpSession session;

	@Override
	public void deleteBookmarkById(Long id) {
		
		bookkmarkRepository.deleteById(id);
	}
	
	@Override
	public List<BookmarkDto> findMyPageBookmarks(BookmarkRequest dto) {
		
		List<BookmarkDto> list = mypageRepository.findMyPageBookmarks(
				dto.getMemberId(),dto.getPageType()
		);
		
		return list;
	}
	
	@Override
	public Map<String, Object> findMyBookmark(MyRequestParams params) {
		
		Sort sort = Sort.by("createsAt").descending();
		
		Pageable pageable = PageRequest.of(params.getPage()-1,params.getSize());
		
		Page<Map<String, Object>> list = mypageRepository.findMyBookmark(
				params.getMemberId(), 
				params.getPageType(),
				params.getStartDate(), 
				params.getEndDate(), 
				pageable);

		Map<String, Object> map = new HashMap<>();
		map.put("list", list.getContent());
        map.put("page", params.getPage());
        map.put("maxPage", list.getTotalPages());
        
        int startPage = ((params.getPage() - 1) /params.getSize()) * params.getSize()  + 1;
        int endPage = startPage + params.getSize() - 1;
        if (endPage > list.getTotalPages()) endPage = list.getTotalPages();
        
        map.put("startPage", startPage);        
        map.put("endPage", endPage);                
        map.put("totalCount", list.getTotalElements());
		
		return map;		
	}	

	@Override
	public List<Map<String, Object>> findMyVote(int page, int size, String startDate, String endDate) {
		
		MemberDto memberDto = Common.idCheck(session);
		
		List<Map<String, Object>> list  = mypageRepository.findMyIdols(memberDto.getId(), startDate, endDate);
		
		return list;  
	}

	@Transactional
	@Override
	public void deleteVoteById(Long id) {
		
		voteDetailRepository.deleteById(id);
		voteRepository.deleteById(id);
	}

	// 내 주문내역 조회
	@Override
	public Map<String, Object> findMyOrders(MyRequestParams params) {
		
	    /**
	     * 정렬설정
	     */
//		Sort sort = Sort.by("status").descending().and(Sort.by("createdAt").ascending());
		
		Pageable pageable = PageRequest.of(params.getPage()-1,params.getSize());
		
		Page<GoodsOrdersDto> pageList = mypageRepository.findMyOrders(
				params.getMemberId(), params.getStartDate(), params.getEndDate(),
				pageable);
		
		//System.out.println("pageList.getContent() : "+pageList.getContent());
		
		Map<String, Object> map = new HashMap<>();
		map.put("list", pageList.getContent());
        map.put("page", params.getPage());
        map.put("maxPage", pageList.getTotalPages());
        
        int startPage = ((params.getPage() - 1) /params.getSize()) * params.getSize()  + 1;
        int endPage = startPage + params.getSize() - 1;
        if (endPage > pageList.getTotalPages()) endPage = pageList.getTotalPages();
        
        map.put("startPage", startPage);        
        map.put("endPage", endPage);                
        map.put("totalCount", pageList.getTotalElements());
		
		return map;
	}

	@Override
	public Map<String, Object> findMySales(MyRequestParams params) {
		
	    /**
	     * 정렬설정
	     */
//		Sort sort = Sort.by("status").descending().and(Sort.by("createdAt").ascending());
		
		Pageable pageable = PageRequest.of(params.getPage(),params.getSize());
		
		Page<GoodsDto> pageList = mypageRepository.findMySales(
				params.getMemberId(), params.getStartDate(), params.getEndDate(),
				pageable);
		
		//System.out.println("pageList.getContent() : "+pageList.getContent());
		
		Map<String, Object> map = new HashMap<>();
		map.put("list", pageList.getContent());
        map.put("page", params.getPage());
        map.put("maxPage", pageList.getTotalPages());
        
        int startPage = ((params.getPage() - 1) /params.getSize()) * params.getSize()  + 1;
        int endPage = startPage + params.getSize() - 1;
        if (endPage > pageList.getTotalPages()) endPage = pageList.getTotalPages();
        
        map.put("startPage", startPage);        
        map.put("endPage", endPage);                
        map.put("totalCount", pageList.getTotalElements());
		
		return map;
	}

}
