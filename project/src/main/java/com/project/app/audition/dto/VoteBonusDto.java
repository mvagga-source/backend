package com.project.app.audition.dto;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "vote_bonus")
public class VoteBonusDto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vote_bonus_seq_gen")
    @SequenceGenerator(name = "vote_bonus_seq_gen", sequenceName = "VOTE_BONUS_SEQ", allocationSize = 1)
	@Column(name = "bonus_id")
    private Long bonusId;

    // 가산점 적용 회차
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audition_id", nullable = false)
    private AuditionDto audition;

    // 어느 대결 결과로 발생한 가산점인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private TeamMatchDto teamMatch;

    // 가산점 받는 아이돌
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idol_id", nullable = false)
    private IdolDto idol;

    // 가산점 고정 표수 ex) 500 = +500표
    @Builder.Default
    @Column(name = "bonus_votes", nullable = false)
    private Long bonusVotes = 0L;

    // 가산점 사유 ex) "A조 팀경연 승리"
    @Column(length = 200)
    private String reason;

    @CreationTimestamp
    @Column(updatable = false, name = "create_at")
    private Timestamp createAt;
}
