package com.project.app.audition.dto; // 패키지 경로는 기존과 동일하게 맞추세요

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "support_log")
public class SupportMember {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

	// 어떤 '서포트 프로젝트'에 참여했는지 연결 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_id", nullable = false)
    private SupportProjectDto supportProject;

    // 후원자 닉네임 (React에서 입력받거나 세션에서 가져올 값)
    @Column(nullable = false, length = 100)
    private String nickname;
    // 후원 금액
    @Column(nullable = false)
    private Long amount;

    // 후원 날짜 및 시간
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Timestamp createdAt;
}