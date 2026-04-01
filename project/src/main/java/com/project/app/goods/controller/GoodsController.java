package com.project.app.goods.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goods.service.GoodsService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired GoodsService goodsService;
    @Autowired HttpSession session;

    // 상품 목록 조회 (페이징 + 검색)
    @ResponseBody
    @GetMapping("/list")
    public AjaxResponse glist(
            @RequestParam(name="page", defaultValue="1") int page,
            @RequestParam(name="size", defaultValue="8") int size,
            @RequestParam(name="category", required=false, defaultValue="") String category,
            @RequestParam(name="search", defaultValue="") String search,
            @RequestParam(name="minPrice", defaultValue="0") int minPrice,
            @RequestParam(name="maxPrice", defaultValue="0") int maxPrice,
            @RequestParam(name="sortDir", defaultValue="DESC") String sortDir,
            @RequestParam(name="view", defaultValue="ALL") String view) {
    	
        Map<String, Object> map = goodsService.findAll(page, size, minPrice, maxPrice, category, search, sortDir, view);
        return AjaxResponse.success(map);
    }
    

    // 상품 상세 조회
    @ResponseBody
    @GetMapping("/view")
    public AjaxResponse view(@RequestParam(name="gno") Long gno) {
        GoodsDto goods = goodsService.findById(gno);
        return AjaxResponse.success(goods);
    }
    
    /**
     * 상품 조회만
     * @param gno
     * @return
     */
    @ResponseBody
    @GetMapping("/detail")
    public AjaxResponse detail(@RequestParam(name="gno") Long gno) {
    	MemberDto memberDto = Common.idCheck(session);
        GoodsDto goods = goodsService.findById(gno);
        return AjaxResponse.success(goods);
    }
    
    // 배너
    @ResponseBody
    @GetMapping("/bannerList")
    public AjaxResponse getBannerList(@RequestParam(name="limit", defaultValue="5") int limit) {
        return AjaxResponse.success(goodsService.findBannerList(limit));
    }

    // 상품 등록 (이미지 업로드 포함)
    @ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(GoodsDto gdto, 
    						@RequestParam(value="gimgFile", required=false) MultipartFile gimgFile) {
        MemberDto memberDto = Common.idCheck(session);
        // 파일 필수 입력 체크
        if(gimgFile == null || gimgFile.isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "대표 상품 이미지는 필수입니다.");
        }else if(gdto.getIdol() == null || gdto.getIdol().getProfileId() == null || gdto.getIdol().getProfileId() <= 0) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "참가자를 입력해주세요.");
        }
        saveValidation(gimgFile, gdto);
        gdto.setMember(memberDto);
        goodsService.save(gdto, gimgFile);
        return AjaxResponse.success();
    }
    
	// 상품 수정 (재고와 판매상태 및 반품주소 등 일부만 수정 가능)
    @ResponseBody
    @PostMapping("/update")
    public AjaxResponse update(GoodsDto gdto, 
    						@RequestParam(value="gimgFile", required=false) MultipartFile gimgFile) {
        MemberDto memberDto = Common.idCheck(session);
        saveValidation(gimgFile, gdto);
        gdto.setMember(memberDto);
        goodsService.update(gdto, gimgFile, memberDto);
        return AjaxResponse.success();
    }

    // 상품 삭제 (Soft Delete)
    @ResponseBody
    @PostMapping("/delete")
    public AjaxResponse delete(@RequestParam(name="gno") Long gno) {
    	
    	System.out.println("gno : "+gno);
    	
        MemberDto memberDto = Common.idCheck(session);
        goodsService.delete(gno, memberDto);
        return AjaxResponse.success();
    }
    
    public void saveValidation(MultipartFile gimgFile, GoodsDto gdto) throws BaCdException {
        // 상품명 체크
        if(gdto.getGname() == null || gdto.getGname().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "상품명을 입력해주세요.");
        }
        // 가격 체크
        else if(gdto.getPrice() == null || gdto.getPrice() <= 0) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "가격을 입력해주세요.");
        }
        // 재고 수량 체크
        else if(gdto.getStockCnt() == null || gdto.getStockCnt() < 0) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "재고 수량을 입력해주세요.");
        }

        // 배송비 체크
        else if(gdto.getGdelPrice() == null || gdto.getGdelPrice() < 0) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "배송비를 입력해주세요.");
        }

        else if(gdto.getGdelType() == null || gdto.getGdelType().trim().isEmpty()) {
        	throw new BaCdException(ErrorCode.INPUT_EMPTY, "택배사를 입력해주세요.");
        }
        
        // 판매 상태 체크 (판매중, 품절 등)
        else if(gdto.getStatus() == null || gdto.getStatus().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "판매 상태를 선택해주세요.");
        }
        
        // 화이트리스트 검증: "판매중", "품절", "숨김" 외의 값이 들어오면 에러
        if(!gdto.getStatus().equals("판매중") && !gdto.getStatus().equals("품절") && !gdto.getStatus().equals("판매중지")) {
            throw new BaCdException(ErrorCode.INVALID_PARAMETER, "잘못된 판매 상태 값입니다.");
        }

        // 출고지 주소 체크
        else if(gdto.getGdelivAddr() == null || gdto.getGdelivAddr().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "출고지 주소를 입력해주세요.");
        }

        // 반품 주소 체크
        else if(gdto.getGdelivAddrReturn() == null || gdto.getGdelivAddrReturn().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "반품 주소를 입력해주세요.");
        }

        // 반품 상세 주소 체크
        else if(gdto.getGdelivAddrReturnDetail() == null || gdto.getGdelivAddrReturnDetail().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "반품 상세 주소를 입력해주세요.");
        }

        // 상세 내용(에디터) 체크
        else if(gdto.getGcontent() == null || gdto.getGcontent().trim().isEmpty() || gdto.getGcontent().equals("<p></p>")) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "상품 상세 설명을 입력해주세요.");
        }
	}
}