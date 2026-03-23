package com.project.app.goodsReviewLike.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.exception.BaCdException;
import com.project.app.goodsReviewLike.dto.GoodsReviewLikeDto;
import com.project.app.goodsReviewLike.repository.GoodsReviewLikeRepository;
import com.project.app.goodsreview.dto.GoodsReviewDto;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class GoodsReviewLikeServiceImpl implements GoodsReviewLikeService {
	
	@Autowired GoodsReviewLikeRepository goodsReviewLikeRepository;
	
	@Transactional
	@Override
	public GoodsReviewLikeDto save(Long grno, MemberDto member) throws BaCdException {
	    // 1. 이미 눌렀는지 확인
	    Optional<GoodsReviewLikeDto> alreadyLike = goodsReviewLikeRepository.findByReview_GrnoAndMember_Id(grno, member);
	    
	    if (alreadyLike.isPresent()) {
	    	goodsReviewLikeRepository.delete(alreadyLike.get()); // 이미 있으면 취소(삭제)
	        return null;
	    } else {
	        // 없으면 추가
	        GoodsReviewLikeDto like = GoodsReviewLikeDto.builder()
	            .review(GoodsReviewDto.builder().grno(grno).build())
	            .member(member)
	            .build();
	        return goodsReviewLikeRepository.save(like);
	    }
	}
}
