package com.project.app.audition.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "audition")
public class AuditionDto {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditionId;

    // 회차 번호 (1, 2, 3...)
    @Column(nullable = false)
    private Integer round;

    // 오디션 제목 ex) "2차 오디션"
    @Column(nullable = false, length = 100)
    private String title;

    // 투표 시작일
    @Column(nullable = false)
    private LocalDate startDate;

    // 투표 마감일
    @Column(nullable = false)
    private LocalDate endDate;

    // 1인당 최대 투표 가능 인원 (기본 7명, 관리자 조정)
    @Builder.Default
    @Column(nullable = false)
    private Integer maxVoteCount = 7;

    // 팀경연 여부 (1차 false, 2~5차 true)
    @Builder.Default
    @Column(nullable = false)
    private Boolean hasTeamMatch = false;

    // 팀경연 승리 시 가산점 비율 ex) 5.00 = +5%
    @Builder.Default
    @Column(precision = 5, scale = 2)
    private BigDecimal bonusRate = BigDecimal.valueOf(5.00);

    // 오디션 진행상태 upcoming / ongoing / ended
    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "upcoming";
    
    // 생존자 커트라인 (예: 30 → 상위 30명 생존, null이면 미설정)
    @Column
    private Integer survivorCount;

    @CreationTimestamp
    @Column(updatable = false, name = "create_at")
    private Timestamp createAt;
}
