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
import org.springframework.transaction.annotation.Transactional;

import com.project.app.board.dto.BoardDto;
import com.project.app.board.repository.BoardRepository;
import com.project.app.common.exception.BaCdException;


@Service
@Transactional(rollbackFor = BaCdException.class)
public class BoardServiceImpl implements BoardService {

	@Autowired BoardRepository boardRepository;

	@Override
	public Map<String, Object> findAll(int page, int size, String category, String search) throws BaCdException {
		Sort sort = Sort.by(Sort.Order.desc("bno"), Sort.Order.desc("bdate"));	//정렬
		Pageable pageable = PageRequest.of(page-1, size, sort);

		Page<BoardDto> pageList;
		if (category.equals("btitle")) {
	        pageList = boardRepository.findByBtitleContaining(search, pageable);
	    } else if (category.equals("bcontent")) {
	    	pageList = boardRepository.findByBcontentContaining(search, pageable);
	    } else if (category.equals("") && !search.equals("")) {
	        pageList = boardRepository.findByBtitleContainingOrBcontentContaining(search, search, pageable);
	    } else {
	    	pageList = boardRepository.findAll(pageable);
	    }
		List<BoardDto> list = pageList.getContent();
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

	@Transactional
	@Override
	public BoardDto save(BoardDto bdto) throws BaCdException {
		bdto.setBhit(0);
		BoardDto board = boardRepository.save(bdto);
		return board;
	}

	@Override
	public BoardDto findById(BoardDto bdto) throws BaCdException {
		BoardDto boardDto = boardRepository.findById(bdto.getBno()).orElseGet(()->{
			return null;		//없을때 null로 리턴
		});
		boardDto.setBhit(boardDto.getBhit()+1);
		return boardDto;
	}

	@Override
	public void deleteById(BoardDto bdto) throws BaCdException {
		boardRepository.deleteById(bdto.getBno());
	}
}
