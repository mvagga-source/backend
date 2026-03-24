package com.project.app.goodsReviewLike.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.auth.dto.MemberDto;
import com.project.app.goodsReviewLike.dto.GoodsReviewLikeDto;

public interface GoodsReviewLikeRepository extends JpaRepository<GoodsReviewLikeDto, Long> {

	public Optional<GoodsReviewLikeDto> findByReview_GrnoAndMember_Id(Long grno, String id);

	// 특정 리뷰의 전체 좋아요 개수 카운트
	public long countByReview_Grno(Long grno);

    // 특정 유저가 이 리뷰를 좋아했는지 존재 여부 확인
	public boolean existsByReview_GrnoAndMember_Id(Long grno, String memberId);
}
