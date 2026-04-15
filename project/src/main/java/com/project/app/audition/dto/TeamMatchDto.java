package com.project.app.audition.dto;

import java.math.BigDecimal;
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
@Table(name = "team_match")
public class TeamMatchDto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_match_seq_gen")
    @SequenceGenerator(name = "team_match_seq_gen", sequenceName = "TEAM_MATCH_SEQ", allocationSize = 1)
	@Column(name = "match_id")
    private Long matchId;

    // 소속 오디션 회차
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audition_id", nullable = false)
    private AuditionDto audition;

    // 대결 이름 ex) "A조", "B조"
    @Column(name = "match_name", nullable = false, length = 50)
    private String matchName;

    // A팀
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_a_id", nullable = false)
    private TeamDto teamA;

    // B팀
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_b_id", nullable = false)
    private TeamDto teamB;

    // A팀 득표율 ex) 40.00
    @Builder.Default
    @Column(name = "team_a_score", precision = 5, scale = 2)
    private BigDecimal teamAScore = BigDecimal.ZERO;

    // B팀 득표율 ex) 60.00
    @Builder.Default
    @Column(name = "team_b_score", precision = 5, scale = 2)
    private BigDecimal teamBScore = BigDecimal.ZERO;

    // 승리팀 (경연 전이면 NULL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_team_id")
    private TeamDto winnerTeam;

    // pending / done
    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "pending";

    @CreationTimestamp
    @Column(updatable = false, name = "create_at")
    private Timestamp createAt;
}
