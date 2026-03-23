package com.project.app.goodsReviewLike.dto;

import java.sql.Timestamp;

import com.project.app.auth.dto.MemberDto;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goodsreview.dto.GoodsReviewDto;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data				//getter/setter
@AllArgsConstructor	//전체생성자
@NoArgsConstructor	//기본생성자
@Builder			//부분생성자
@Entity
@Table(name="goods_review_like")
public class GoodsReviewLikeDto {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grlno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grno")
    private GoodsReviewDto review; // 리뷰

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private MemberDto member; // '도움돼요' 누른사람
}
