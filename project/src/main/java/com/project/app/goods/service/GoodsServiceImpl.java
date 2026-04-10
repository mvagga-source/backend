package com.project.app.goods.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;
import com.project.app.common.Common;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goods.repository.GoodsRepository;
import com.project.app.goodsreview.repository.GoodsReviewRepository;
import com.project.app.goodsreview.service.GoodsReviewService;

import jakarta.servlet.http.HttpSession;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class GoodsServiceImpl implements GoodsService {
	/*삭제시 주문정보와 꼬일 수 있어서 del_yn 적용*/

    @Autowired GoodsRepository goodsRepository;
    
    @Autowired HttpSession session;
    
    @Autowired
	private GoodsReviewRepository goodsReviewRepository;

    @Override
    public Map<String, Object> findAll(int page, int size, int minPrice, int maxPrice, 
    		String category, String search, String sortDir, String view) throws BaCdException {
    	// 정렬 조건 생성 (기본값은 최신순)
        Sort sort = Sort.by(Sort.Direction.DESC, "gno");
        if ("priceAsc".equals(sortDir)) {
            // 낮은 가격순: 가격 오름차순, 같은 가격이면 최신순
            sort = Sort.by(Sort.Order.asc("price"), Sort.Order.desc("gno"));
        } else if ("priceDesc".equals(sortDir)) {
            // 높은 가격순: 가격 내림차순, 같은 가격이면 최신순
            sort = Sort.by(Sort.Order.desc("price"), Sort.Order.desc("gno"));
        } else if ("ASC".equals(sortDir)) {
            // 오래된 순 (필요시)
            sort = Sort.by(Sort.Direction.ASC, "gno");
        }
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        
        Page<GoodsDto> pageList;
        if("ALL".equals(view))
        	pageList = goodsRepository.findGoodsWithFilters(category, search, minPrice, maxPrice, pageable);
        else {
        	MemberDto memberDto = Common.idCheck(session);
        	pageList = goodsRepository.findMyGoodsWithFilters(category, search, minPrice, maxPrice, pageable, memberDto.getId());
        }
        
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
        return goodsRepository.findById(gno).filter(g -> g.getDelYn().equals("n")).orElseThrow(() -> new BaCdException(ErrorCode.PAGE_EMPTY, "상품 정보가 존재하지 않습니다."));
    }

    @Override
    @Transactional
    public GoodsDto save(GoodsDto gdto, MultipartFile gimgFile) throws BaCdException {
        // 초기 설정
        if(gdto.getDelYn() == null) gdto.setDelYn("n");
        String filePath = Common.saveFile(gimgFile, "goods");
        gdto.setGimg(filePath);
        //방어코드
        if(gdto.getGdelPrice()==0) {
        	gdto.setStatus("품절");				//개수가 0인 경우 판매상태 변경
        }else if(gdto.getStatus().equals("품절")) {
        	gdto.setStockCnt(0L);				//품절상태이면 재고수량 0로 변경
        }
        return goodsRepository.save(gdto);
    }
    
    @Override
    @Transactional
    public GoodsDto update(GoodsDto gdto, MultipartFile gimgFile, MemberDto member) throws BaCdException {
    	//상품을 다른 상품으로 교체할 경우의 문제(관리자 승인(외래키 조인으로 문제있음) 또는 수정안되고 삭제와 등록만 유도)
    	//잘못 입력 및 재고수량 보충 등만 수정가능하게(판매상태와 재고수량 및 반품주소만 가능)
    	//신고기능도 필요
    	//재고 보충시 저장 누르기 전 사이에 회원이 구입하는 경우 수량과 일치하지 않을 수 있음 -> 재고수량이 없는데 주문이 들어온 경우 관리자에게 취소 요청
    	GoodsDto goods = goodsRepository.findById(gdto.getGno()).filter(g -> g.getDelYn().equals("n")).orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "상품 정보가 존재하지 않습니다."));
    	if(!gdto.getMember().getId().equals(member.getId())) throw new BaCdException(ErrorCode.AUTH_USER_NOT_MATCH);		//작성자가 맞는지 확인
    	/*Common.deleteFile(goods.getGimg(), "goods");
        String filePath = Common.saveFile(gimgFile, imgHostUrl, "goods");
        gdto.setGimg(filePath);
        goods.setGname(gdto.getGname());
        goods.setGcontent(gdto.getGcontent());
        goods.setPrice(gdto.getPrice());*/
        goods.setStockCnt(gdto.getStockCnt());		//재고를 채우거나 품절
        goods.setGdelPrice(gdto.getGdelPrice());	//배송비 변경
        goods.setStatus(gdto.getStatus());	//판매상태 변경
        //방어코드
        if(gdto.getGdelPrice()==0) {
        	goods.setStatus("품절");				//개수가 0인 경우 판매상태 변경
        }else if(gdto.getStatus().equals("품절")) {
        	goods.setStockCnt(0L);				//품절상태이면 재고수량 0로 변경
        }
        goods.setGdelType(gdto.getGdelType());		//택배사 변경
        goods.setGdelivAddr(gdto.getGdelivAddr());	//출고지주소 변경
        goods.setGdelivAddrReturn(gdto.getGdelivAddrReturn());				//반송주소 변경
        goods.setGdelivAddrReturnDetail(gdto.getGdelivAddrReturnDetail());	//반송상세주소 변경
        return goodsRepository.save(goods);
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
        goods.setDelYn("y");		//삭제여부만 y로 변경, 리뷰와 나머지도 그대로 놔두기(삭제하면 주문정보와 꼬임)
    }

    @Override
    public Map<String, Object> findBannerList(int limit) {
        // 평점 높은 순으로 상위 limit개만 가져옴
        Pageable pageable = PageRequest.of(0, limit);
        List<Map<String, Object>> bannerList = goodsRepository.findTopRatedBannerList(pageable);
        Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", bannerList);
        return map;
    }


}