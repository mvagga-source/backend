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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idolId;

    // 참가 오디션 회차(AuditionDto 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audition_id", nullable = false)
    private AuditionDto audition;

    // 참가자 정보(IdolProfileDto 연결) (팀원 완성 후 @ManyToOne으로 교체 예정)
    @Column(name = "idol_profile_id")
    private Long idolProfileId;
    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "idol_profile_id", nullable = false)
//    private IdolProfileDto idolProfile;

    // active 생존 / eliminated 탈락 / winner 최종합격
    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "active";

    // 탈락 회차 (생존 중이면 NULL)
    private Integer eliminatedRound;

    // 탈락 처리 시각
    private Timestamp eliminatedAt;

    @CreationTimestamp
    @Column(updatable = false, name = "create_at")
    private Timestamp createAt;
}
