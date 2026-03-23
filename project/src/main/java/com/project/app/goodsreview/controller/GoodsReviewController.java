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
import com.project.app.common.exception.BaCdException;
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
			@RequestParam(name="lastCno", required=false, defaultValue="0") Long lastCno) {
        return AjaxResponse.success(goodsReviewService.findAll(gno, size, lastCno));
    }

    /**
     * 리뷰 등록 (파일 업로드 포함)
     * FormData로 보낼 때: data(JSON string), file(MultipartFile)
     */
	@ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(
            GoodsReviewDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpSession session) throws BaCdException {
		MemberDto member = Common.idCheck(session);
        GoodsReviewDto savedReview = goodsReviewService.save(dto, file, member);
        return AjaxResponse.success(savedReview);
    }

    /**
     * 리뷰 수정
     */
	@ResponseBody
    @PostMapping("/update")
    public AjaxResponse update(
            @RequestPart("review") GoodsReviewDto dto,
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
    @PostMapping("/delete/{grno}")
    public AjaxResponse delete(GoodsReviewDto dto,
    		HttpSession session) throws BaCdException {
		MemberDto member = Common.idCheck(session);
        goodsReviewService.delete(dto, member);
        return AjaxResponse.success();
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
