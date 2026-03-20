package com.project.app.boardlike.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;
import com.project.app.board.repository.BoardRepository;
import com.project.app.boardcomment.repository.BoardCommentRepository;
import com.project.app.boardlike.dto.BoardLikeDto;
import com.project.app.boardlike.repository.BoardLikeRepository;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;


@Service
@Transactional(rollbackFor = BaCdException.class)
public class BoardLikeServiceImpl implements BoardLikeService {

	@Autowired BoardLikeRepository boardLikeRepository;
	
	@Autowired BoardRepository boardRepository;

	@Transactional
	@Override
	public Map<String, Object> save(BoardLikeDto dto, MemberDto member) throws BaCdException {
		Optional<BoardLikeDto> existingLike =boardLikeRepository.findByBoardBnoAndMemberId(dto.getBoard().getBno(), member.getId());

	    Integer myLike = null;

	    if (existingLike.isPresent()) {
	        BoardLikeDto current = existingLike.get();

	        if (current.getIsLike().equals(dto.getIsLike())) {
	            boardLikeRepository.delete(current);
	            myLike = null; // 취소
	        } else {
	            current.setIsLike(dto.getIsLike());
	            boardLikeRepository.save(current);
	            myLike = dto.getIsLike(); // 변경
	        }

	    } else {
	        dto.setMember(member);
	        boardLikeRepository.save(dto);
	        myLike = dto.getIsLike(); // 등록
	    }
	    Map<String, Object> result = new HashMap<>();
	    result.put("myLike", myLike);
	    return result;
	}

	@Override
	public BoardLikeDto view(BoardLikeDto dto) throws BaCdException {
		BoardLikeDto boardLikeDto = boardLikeRepository.findById(dto.getLbno()).orElseGet(()->{return null;});
		return boardLikeDto;
	}

	@Override
	public BoardLikeDto findById(BoardLikeDto dto) throws BaCdException {
		BoardLikeDto boardLikeDto = boardLikeRepository.findById(dto.getLbno()).orElseGet(()->{return null;});
		return boardLikeDto;
	}

	@Transactional
	@Override
	public void delete(BoardLikeDto dto, MemberDto member) throws BaCdException {
		BoardLikeDto boardLikeDto = boardLikeRepository.findById(dto.getLbno()).orElseGet(()->{return null;});
	}
}
