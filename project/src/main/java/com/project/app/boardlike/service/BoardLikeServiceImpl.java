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

	@Transactional
	@Override
	public String save(BoardLikeDto dto, MemberDto member) throws BaCdException {
		// 1. 기존에 이 사용자가 이 게시글에 남긴 추천/비추천 기록이 있는지 조회
		Optional<BoardLikeDto> existingLike = boardLikeRepository.findByBoardBnoAndMemberId(dto.getBoard().getBno(), member.getId());

	    if (existingLike.isPresent()) {
	        BoardLikeDto current = existingLike.get();
	        
	        // 같은 버튼을 또 눌렀을 때 (예: 추천인데 추천 또 클릭) -> '취소' (삭제)
	        if (current.getIsLike().equals(dto.getIsLike())) {
	            boardLikeRepository.delete(current);
	            return "취소되었습니다.";
	        } 
	        // 다른 버튼을 눌렀을 때 (예: 추천 상태인데 비추천 클릭) -> '변경' (수정)
	        else {
	            current.setIsLike(dto.getIsLike());
	            boardLikeRepository.save(current); // JPA가 ID를 확인하고 Update 실행
	            return "변경되었습니다.";
	        }
	    } else {
	        // 2. 기록이 없으면 새로 저장 (추천 or 비추천)
	    	dto.setMember(member);
	        boardLikeRepository.save(dto);
	        return "등록되었습니다.";
	    }
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
