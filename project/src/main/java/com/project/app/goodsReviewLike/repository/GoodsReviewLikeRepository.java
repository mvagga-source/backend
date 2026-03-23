package com.project.app.goodsReviewLike.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.auth.dto.MemberDto;
import com.project.app.goodsReviewLike.dto.GoodsReviewLikeDto;

public interface GoodsReviewLikeRepository extends JpaRepository<GoodsReviewLikeDto, Long> {

	Optional<GoodsReviewLikeDto> findByReview_GrnoAndMember_Id(Long grno, MemberDto member);

}
