package com.project.app.notificationSetting.dto;

import com.project.app.auth.dto.MemberDto;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@DynamicInsert // null인 필드는 제외하고 insert (DB의 ColumnDefault 적용을 위해)
@Table(name = "notification_setting")
public class NotificationSettingDto {

    @Id
    private String id; // MemberDto의 id와 동일하게 맞춤 (Shared Primary Key 방식)

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // MemberDto의 ID를 이 엔티티의 PK로 사용(별도의 pk없이 memberId를 pk로 사용)
    @JoinColumn(name = "member_id")
    private MemberDto member;

    // --- 커뮤니티 알림 설정 ---
    
    @Column(name = "allow_board_comment", length = 1)
    @ColumnDefault("'y'")
    private String allowBoardComment = "y"; // 내 글 댓글 및 내 댓글 답글 알림

    @Column(name = "allow_board_like", length = 1)
    @ColumnDefault("'y'")
    private String allowBoardLike = "y";    // 내 게시글 추천 알림

    // --- 굿즈샵(장터) 알림 설정 ---

    @Column(name = "allow_goods_review", length = 1)
    @ColumnDefault("'y'")
    private String allowGoodsReview = "y";  // 내 상품 리뷰 및 내 리뷰 답글 알림

    @Column(name = "allow_goods_trade", length = 1)
    @ColumnDefault("'y'") 
    private String allowGoodsTrade = "y";   // 거래 상태 알림 (결제/배송 등 - 필수라면 관리만 하고 UI에서 제외 가능)
    
    @Column(name = "allow_goods_review_like", length = 1)
    @ColumnDefault("'y'") 
    private String allowGoodsReviewLike = "y";   // 리뷰 도움돼요 알림

    // --- 시스템 및 기타 ---
    
    @Column(name = "allow_qna_answer", length = 1)
    @ColumnDefault("'y'")
    private String allowQnaAnswer = "y";    // 1:1 문의 답변 알림 (필수권장이므로 항상 y 유지 로직 필요)
    
    /*@UpdateTimestamp
    @Column(name = "updt")
    private Timestamp updt;*/		//수정일
}