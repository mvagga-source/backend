package com.project.app.goodsreview.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.exception.BaCdException;
import com.project.app.goodsreview.dto.GoodsReviewDto;

public interface GoodsReviewService {
    // 주문 번호(gono)로 작성된 리뷰 단건 조회
    GoodsReviewDto findByGono(Long gono, MemberDto member) throws BaCdException;
	
	// 리뷰 등록 (이미지 포함)
    public GoodsReviewDto save(GoodsReviewDto dto, Long gono, MultipartFile file, MemberDto member) throws BaCdException;
    
    // 리뷰 수정
    public GoodsReviewDto update(GoodsReviewDto dto, MultipartFile file, MemberDto member) throws BaCdException;
    
    // 리뷰 논리 삭제 (del_yn = 'y')
    public GoodsReviewDto delete(GoodsReviewDto dto, MemberDto member) throws BaCdException;
    
    //답글 등록
    public GoodsReviewDto reply(GoodsReviewDto dto, MemberDto member) throws BaCdException;
    
    // 특정 상품의 평균 별점 조회
    public Double getAverageRating(Long gno) throws BaCdException;

    public Map<String, Object> findAll(Long gno, int size, Long lastGrno, String sortDir, Long lastLikeCnt, Double lastRating) throws BaCdException;
}
