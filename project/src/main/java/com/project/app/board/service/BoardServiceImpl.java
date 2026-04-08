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
public class BoardServiceImpl implements BoardService {

	@Autowired BoardRepository boardRepository;

	@Autowired BoardCommentRepository commentRepository;
	
	@Autowired BoardLikeRepository boardLikeRepository;

	@Override
	public Map<String, Object> findAll(int page, int size, String category, String search, String sortDir) throws BaCdException {
		Sort sort = Sort.by(Sort.Order.desc("bno"), Sort.Order.desc("bdate"));	//정렬
		if ("ASC".equals(sortDir)) {
			sort = Sort.by(Sort.Direction.ASC, "bno");
		}else if ("bhit".equals(sortDir)) {
			sort = Sort.by(Sort.Order.desc("bhit"), Sort.Order.desc("bno"));
		}
		Pageable pageable = PageRequest.of(page-1, size, sort);

		Page<BoardDto> pageList;
		if (category.equals("btitle")) {
	        pageList = boardRepository.findByBtitleContainingAndReportYnAndDelYn(search, "n", "n", pageable);
	    } else if (category.equals("bcontent")) {
	    	pageList = boardRepository.findByBcontentContainingAndReportYnAndDelYn(search, "n", "n", pageable);
	    } else if ("nickname".equals(category)) {	// 작성자 닉네임으로 검색
	        pageList = boardRepository.findByMemberNicknameContainingAndReportYnAndDelYn(search, "n", "n", pageable);
	    } else if (category.equals("") && !search.equals("")) {
	        pageList = boardRepository.findByBtitleContainingOrBcontentContainingOrMemberNicknameContainingAndReportYnAndDelYn(search, search, search, "n", "n", pageable);
	    } else {
	    	pageList = boardRepository.findByReportYnAndDelYn("n", "n", pageable);
	    }
		List<BoardDto> list = pageList.getContent();
		int maxPage = pageList.getTotalPages();
		int displayCount = 5; // 한 번에 보여줄 페이지 번호 개수
		int startPage = ((page-1)/displayCount)*displayCount+1;						//0-10:1, 11-20:11
		int endPage = Math.min(startPage+(displayCount-1), maxPage);			//0-10:10, 11-20:20
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", pageList.getTotalElements()); // 전체 게시글 수
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
	public Map<String, Object> view(BoardDto bdto, MemberDto member) throws BaCdException {
		BoardDto boardDto = boardRepository.findById(bdto.getBno()).orElseThrow(() -> new BaCdException(ErrorCode.PAGE_EMPTY, "게시판 정보가 존재하지 않습니다."));
		if ("y".equals(boardDto.getReportYn())) {
		    throw new BaCdException(ErrorCode.PAGE_EMPTY, "신고 처리된 게시글입니다.");
		}else if("y".equals(boardDto.getDelYn())) {
			throw new BaCdException(ErrorCode.PAGE_EMPTY, "삭제 처리된 게시글입니다.");
		}
		boardDto.setBhit(boardDto.getBhit()+1);
		
		Integer myLike = null;
		//로그인한 경우만 내가 추천한 상태 조회
		if (member != null) {
	        BoardLikeDto like = boardLikeRepository
	            .findByBoardBnoAndMemberId(bdto.getBno(), member.getId())
	            .orElse(null);

	        if (like != null) {
	            myLike = like.getIsLike(); // 1 or -1
	        }
	    }

	    //응답 구성
	    Map<String, Object> result = new HashMap<>();
	    result.put("board", boardDto);
	    result.put("myLike", myLike);

	    return result;
	}

	@Override
	public BoardDto findById(BoardDto bdto) throws BaCdException {
		BoardDto boardDto = boardRepository.findById(bdto.getBno()).orElseGet(()->{
			return null;		//없을때 null로 리턴
		});
		return boardDto;
	}

	@Transactional
	@Override
	public void delete(BoardDto bdto, MemberDto member) throws BaCdException {
		BoardDto boardDto = boardRepository.findById(bdto.getBno()).orElseGet(()->{return null;});
		if(!boardDto.getMember().getId().equals(member.getId())) throw new BaCdException(ErrorCode.AUTH_USER_NOT_MATCH);		//작성자가 맞는지 확인
		//commentRepository.deleteByBoard(bdto);		//댓글들 삭제
		boardRepository.deleteById(bdto.getBno());	//게시글 삭제
	}

	@Transactional
	@Override
	public BoardDto update(BoardDto bdto, MemberDto member) throws BaCdException {
		//다른화면에서 로그인 및 수정된 경우도 있을 수 있고 조회수 등 null값 방지
		BoardDto boardDto = boardRepository.findById(bdto.getBno()).orElseGet(()->{return null;});
		if(!boardDto.getMember().getId().equals(member.getId())) throw new BaCdException(ErrorCode.AUTH_USER_NOT_MATCH);		//작성자가 맞는지 확인
		boardDto.setBtitle(bdto.getBtitle());
		boardDto.setBcontent(bdto.getBcontent());
		return boardDto;
	}
}
