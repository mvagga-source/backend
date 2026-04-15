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
@Table(name = "idol")
public class IdolDto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idol_seq_gen")
    @SequenceGenerator(name = "idol_seq_gen", sequenceName = "IDOL_SEQ", allocationSize = 1)
	@Column(name = "idol_id")
    private Long idolId;

    // 참가 오디션 회차(AuditionDto 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audition_id", nullable = false)
    private AuditionDto audition;

    // 참가자 정보(IdolProfileDto 연결) (팀원 완성 후 @ManyToOne으로 교체 예정)
    @Column(name = "idol_profile_id")
    private Long idolProfileId;
    
    // active 생존 / eliminated 탈락 / winner 최종합격
    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "active";

    // 탈락 회차 (생존 중이면 NULL)
    @Column(name = "eliminated_round")
    private Integer eliminatedRound;

    // 탈락 처리 시각
    @Column(name = "eliminated_at")
    private Timestamp eliminatedAt;

    @CreationTimestamp
    @Column(updatable = false, name = "create_at")
    private Timestamp createAt;
}
