package com.project.app.audition.dto;

import java.sql.Timestamp;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audition_seq_gen")
    @SequenceGenerator(name = "audition_seq_gen", sequenceName = "AUDITION_SEQ", allocationSize = 1)
	@Column(name = "audition_id")
    private Long auditionId;

    // 회차 번호 (1, 2, 3...)
    @Column(nullable = false)
    private Integer round;

    // 오디션 제목 ex) "2차 오디션"
    @Column(nullable = false, length = 100)
    private String title;

    // 투표 시작일
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // 투표 마감일
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // 1인당 최대 투표 가능 인원 (기본 7명, 관리자 조정)
    @Builder.Default
    @Column(name = "max_vote_count", nullable = false)
    private Integer maxVoteCount = 7;

    // 팀경연 여부 (1차 false, 2~5차 true)
    @Builder.Default
    @Column(name = "has_team_match", nullable = false)
    private Boolean hasTeamMatch = false;

    // 팀경연 승리 시 가산점 고정 표수 ex) 500 = +500표
    @Builder.Default
    @Column(name = "bonus_votes")
    private Long bonusVotes = 500L;

    // 오디션 진행상태 upcoming / ongoing / ended
    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "upcoming";
    
    // 생존자 커트라인 (예: 30 → 상위 30명 생존, null이면 미설정)
    @Column(name = "survivor_count")
    private Integer survivorCount;

	// 삭제 여부 (true = 삭제됨, 목록에서 제외)
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    @CreationTimestamp
    @Column(updatable = false, name = "create_at")
    private Timestamp createAt;
}
