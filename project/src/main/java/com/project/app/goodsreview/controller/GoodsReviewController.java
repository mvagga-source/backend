package com.project.app.goodsreview.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.service.MemberService;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;
import com.project.app.goodsreview.dto.GoodsReviewDto;
import com.project.app.goodsreview.service.GoodsReviewService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/goodsReview")
public class GoodsReviewController {
	@Autowired
    private GoodsReviewService goodsReviewService;
	
	@Autowired
	MemberService memberService;

	@Autowired
	HttpSession session;

    /**
     * 리뷰 목록 조회 (특정 상품)
     */
	@ResponseBody
    @GetMapping("/list")
    public AjaxResponse getList(@RequestParam(name="gno", required=false) Long gno,
    		@RequestParam(name="size", required=false, defaultValue="10") int size,
			@RequestParam(name="lastCno", required=false, defaultValue="0") Long lastCno,
			@RequestParam(name="lastLikeCnt", required=false, defaultValue="0") Long lastLikeCnt,
			@RequestParam(name="lastRating", required=false, defaultValue="0")  Double lastRating,
			@RequestParam(name="sortDir", defaultValue="latest") String sortDir) {
        return AjaxResponse.success(goodsReviewService.findAll(gno, size, lastCno, sortDir, lastLikeCnt, lastRating));
    }
	
	/**
	 * 리뷰 상세조회
	 */
	@ResponseBody
    @GetMapping("/detail")
    public AjaxResponse detail(@RequestParam(name = "gono", required = false) Long gono) {
        MemberDto memberDto = Common.idCheck(session);
        GoodsReviewDto review = goodsReviewService.findByGono(gono, memberDto);
        return AjaxResponse.success(review);
    }

    /**
     * 리뷰 등록 (파일 업로드 포함)
     * FormData로 보낼 때: data(JSON string), file(MultipartFile)
     */
	@ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(
    		@RequestParam(name="gno", required=false) Long gno,
    		@RequestParam(name="gono", required=false) Long gono,
            GoodsReviewDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file) throws BaCdException {
		MemberDto member = Common.idCheck(session);
		// 주문 번호(gono) 유효성 및 본인 확인
	    if (dto.getOrder() == null || dto.getOrder().getGono() == null) {
	        throw new BaCdException(ErrorCode.INPUT_EMPTY, "주문 정보가 없습니다.");
	    }
		//dto.setGoods(GoodsDto.builder().gno(gno).build());
        GoodsReviewDto savedReview = goodsReviewService.save(dto, gono, file, member);
        return AjaxResponse.success(savedReview);
    }

    /**
     * 리뷰 수정
     */
	@ResponseBody
    @PostMapping("/update")
    public AjaxResponse update(
            GoodsReviewDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpSession session) throws BaCdException {
		MemberDto member = Common.idCheck(session);
        GoodsReviewDto updatedReview = goodsReviewService.update(dto, file, member);
        return AjaxResponse.success(updatedReview);
    }

    /**
     * 리뷰 삭제 (논리 삭제: del_yn = 'y')
     */
	@ResponseBody
    @PostMapping("/delete")
    public AjaxResponse delete(GoodsReviewDto dto,
    		HttpSession session) throws BaCdException {
		MemberDto member = Common.idCheck(session);
        return AjaxResponse.success(goodsReviewService.delete(dto, member));
    }
	
	/**
	 * 리뷰 답글 등록
	 */
	@ResponseBody
	@PostMapping("/reply")
	public AjaxResponse reply(@RequestParam(name="gno", required=false) Long gno,
	        @RequestParam(name="parent_grno", required=false) Long parentGrno, // 답글일 경우 부모 번호
	        GoodsReviewDto dto) throws BaCdException {
	    MemberDto member = Common.idCheck(session);
	    // 상품 정보 세팅
	    dto.setGoods(GoodsDto.builder().gno(gno).build());
	    // 답글일 경우 부모 객체 세팅 (서비스에서 처리해도 됨)
	    if (parentGrno != null) {
	        dto.setParent(GoodsReviewDto.builder().grno(parentGrno).build());
	    }

	    GoodsReviewDto savedReview = goodsReviewService.reply(dto, member);
	    return AjaxResponse.success(savedReview);
	}

    /**
     * 상품 평균 별점 조회
     */
	@ResponseBody
    @GetMapping("/average/{gno}")
    public AjaxResponse getAverage(@PathVariable Long gno) {
        return AjaxResponse.success(goodsReviewService.getAverageRating(gno));
    }
}
