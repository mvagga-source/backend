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
    public Map<String, Object> findAll(int page, int size, String category, String search) {
        Sort sort = Sort.by(Sort.Direction.DESC, "gno");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<GoodsDto> pageList;
        if (!search.isEmpty()) {
            // 상품명(gcontent의 첫줄 등) 검색 - 쿼리메소드 구현 필요
            pageList = goodsRepository.findByGcontentContainingAndDelYn(search, "n", pageable);
        } else {
            pageList = goodsRepository.findByDelYn("n", pageable);
        }

        List<GoodsDto> list = pageList.getContent();
        int maxPage = pageList.getTotalPages();
		int displayCount = 5; // 한 번에 보여줄 페이지 번호 개수
		int startPage = ((page-1)/displayCount)*displayCount+1;						//0-10:1, 11-20:11
		int endPage = Math.min(startPage+(displayCount-1), maxPage);			//0-10:10, 11-20:20
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("page", page);
		map.put("maxPage", maxPage);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("category", category);
		map.put("search", search);
        return map;
    }

    @Override
    public GoodsDto findById(Long gno) {
        return goodsRepository.findById(gno).filter(g -> g.getDelYn().equals("n")).orElse(null);
    }

    @Override
    @Transactional
    public void save(GoodsDto gdto) {
        // 초기 설정
        if(gdto.getStockCnt() == null) gdto.setStockCnt(0L);
        if(gdto.getDelYn() == null) gdto.setDelYn("n");
        goodsRepository.save(gdto);
    }

    @Override
    @Transactional
    public void delete(Long gno, MemberDto member) {
        GoodsDto goods = findById(gno);
        
        // 판매자 본인 확인
        if(!goods.getMember().getId().equals(member.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_MATCH);
        }
        
        // Soft Delete
        goods.setDelYn("y");
    }
}