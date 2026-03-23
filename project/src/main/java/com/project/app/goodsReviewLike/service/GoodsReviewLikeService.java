package com.project.app.goodsReviewLike.service;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.exception.BaCdException;
import com.project.app.goodsReviewLike.dto.GoodsReviewLikeDto;

public interface GoodsReviewLikeService {
	public GoodsReviewLikeDto save(Long grlno, MemberDto member) throws BaCdException;
}
