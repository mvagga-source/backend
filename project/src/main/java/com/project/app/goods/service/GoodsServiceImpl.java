package com.project.app.goods.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goods.repository.GoodsRepository;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class GoodsServiceImpl implements GoodsService {

    @Autowired GoodsRepository goodsRepository;

    @Override
    public Map<String, Object> findAll(int page, int size, int minPrice, int maxPrice, String category, String search, String sortDir) throws BaCdException {
    	// 1. 정렬 조건 생성 (기본값은 최신순)
        Sort sort = Sort.by(Sort.Direction.DESC, "gno");
        
        if ("priceAsc".equalsIgnoreCase(sortDir)) {
            // 낮은 가격순: 가격 오름차순, 같은 가격이면 최신순
            sort = Sort.by(Sort.Order.asc("price"), Sort.Order.desc("gno"));
        } else if ("priceDesc".equalsIgnoreCase(sortDir)) {
            // 높은 가격순: 가격 내림차순, 같은 가격이면 최신순
            sort = Sort.by(Sort.Order.desc("price"), Sort.Order.desc("gno"));
        } else if ("ASC".equalsIgnoreCase(sortDir)) {
            // 오래된 순 (필요시)
            sort = Sort.by(Sort.Direction.ASC, "gno");
        }
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<GoodsDto> pageList = goodsRepository.findGoodsWithFilters(category, search, minPrice, maxPrice, pageable);
        /*Page<GoodsDto> pageList;
        if (!search.isEmpty()) {
            // 상품명 검색 - 쿼리메소드 구현 필요
            pageList = goodsRepository.findByGnameContainingAndDelYn(search, "n", pageable);
        } else {
            pageList = goodsRepository.findByDelYn("n", pageable);
        }*/

        List<GoodsDto> list = pageList.getContent();
        int maxPage = pageList.getTotalPages();
		int displayCount = 5; // 한 번에 보여줄 페이지 번호 개수
		int startPage = ((page-1)/displayCount)*displayCount+1;						//0-10:1, 11-20:11
		int endPage = Math.min(startPage+(displayCount-1), maxPage);			//0-10:10, 11-20:20
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("totalCount", pageList.getTotalElements()); // 전체 게시글 수
		map.put("page", page);
		map.put("maxPage", maxPage);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("category", category);
		map.put("search", search);
		map.put("sortDir", sortDir); // 리액트가 순번 계산할 때 참고하도록 반환
        return map;
    }

    @Override
    public GoodsDto findById(Long gno) throws BaCdException {
        return goodsRepository.findById(gno).filter(g -> g.getDelYn().equals("n")).orElse(null);
    }

    @Override
    @Transactional
    public GoodsDto save(GoodsDto gdto) throws BaCdException {
        // 초기 설정
        if(gdto.getStockCnt() == null) gdto.setStockCnt(0L);
        if(gdto.getDelYn() == null) gdto.setDelYn("n");
        return goodsRepository.save(gdto);
    }

    @Override
    @Transactional
    public void delete(Long gno, MemberDto member) throws BaCdException {
        GoodsDto goods = findById(gno);
        
        // 판매자 본인 확인
        if(!goods.getMember().getId().equals(member.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_MATCH);
        }
        
        // Soft Delete
        goods.setDelYn("y");
    }
}