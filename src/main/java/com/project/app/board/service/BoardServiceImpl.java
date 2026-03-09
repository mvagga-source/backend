package com.project.app.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.app.board.dto.Board;
import com.project.app.board.repository.BoardRepository;
import com.project.app.user.dto.LoginRequest;
import com.project.app.user.dto.Member;
import com.project.app.user.repository.MemberRepository;


@Service
public class BoardServiceImpl implements BoardService {

	@Autowired BoardRepository boardRepository;

	@Override
	public Map<String, Object> findAll(int page, int size, String category, String search) {
		Sort sort = Sort.by(Sort.Order.desc("bgroup"), Sort.Order.asc("bstep"));	//정렬
		Pageable pageable = PageRequest.of(page-1, size, sort);

		Page<Board> pageList;
		if (category.equals("btitle")) {
	        pageList = boardRepository.findByBtitleContaining(search, pageable);
	    } else if (category.equals("bcontent")) {
	    	pageList = boardRepository.findByBcontentContaining(search, pageable);
	    } else if (category.equals("")) {
	        pageList = boardRepository.findByBtitleContainingOrBcontentContaining(search, search, pageable);
	    } else {
	    	pageList = boardRepository.findAll(pageable);
	    }
		List<Board> list = pageList.getContent();
		int maxPage = pageList.getTotalPages();
		int startPage = ((page-1)/10)*10+1;						//0-10:1, 11-20:11
		int endPage = Math.min(startPage+9, maxPage);			//0-10:10, 11-20:20
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("page", page);
		map.put("maxPage", maxPage);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("category", category);
		map.put("search", search);
		return map;
	}
}
