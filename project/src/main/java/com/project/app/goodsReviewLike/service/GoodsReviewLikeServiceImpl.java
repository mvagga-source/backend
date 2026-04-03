package com.project.app.goodsReviewLike.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goodsReviewLike.dto.GoodsReviewLikeDto;
import com.project.app.goodsReviewLike.repository.GoodsReviewLikeRepository;
import com.project.app.goodsreview.dto.GoodsReviewDto;
import com.project.app.goodsreview.repository.GoodsReviewRepository;
import com.project.app.notification.dto.NotificationDto;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class GoodsReviewLikeServiceImpl implements GoodsReviewLikeService {
	
	@Autowired GoodsReviewLikeRepository goodsReviewLikeRepository;
	
	@Autowired GoodsReviewRepository goodsReviewRepository;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	@Transactional
	@Override
	public GoodsReviewLikeDto save(Long grno, MemberDto member) throws BaCdException {
	    // 1. 이미 눌렀는지 확인
	    Optional<GoodsReviewLikeDto> alreadyLike = goodsReviewLikeRepository.findByReview_GrnoAndMember_Id(grno, member.getId());
	    
	    if (alreadyLike.isPresent()) {
	    	goodsReviewLikeRepository.delete(alreadyLike.get()); // 이미 있으면 취소(삭제)
	        return null;
	    } else {
	        // 없으면 추가
	        GoodsReviewLikeDto like = GoodsReviewLikeDto.builder()
	            .review(GoodsReviewDto.builder().grno(grno).build())
	            .member(member)
	            .build();
	        GoodsReviewLikeDto savedLike = goodsReviewLikeRepository.save(like);
	        
	        // 실제 리뷰 정보를 가져와서 작성자를 확인
	        GoodsReviewDto review = goodsReviewRepository.findById(grno)
	                .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "해당 리뷰가 존재하지 않습니다."));
	        
	        // 본인 리뷰가 아닐 때만 알림 발송
	        if (review.getMember() != null && !review.getMember().getId().equals(member.getId())) {
	            NotificationDto eventData = NotificationDto.builder()
	                    .member(review.getMember()) // 수신자: 리뷰 작성자
	                    .sender(member)             // 발신자: 좋아요 누른 사람
	                    .nocontent(member.getNickname()+"님이 작성하신 리뷰에 **'도움돼요'**를 남겨주셨습니다.")
	                    .type("GOODS_REVIEW_LIKE")   // 타입 지정
	                    .url("/GoodsView/" + review.getGoods().getGno()) // 해당 상품 상세 페이지로 이동
	                    .isRead("n")
	                    .build();
	            eventPublisher.publishEvent(eventData);
	        }
	        
	        return savedLike;
	    }
	}
}
