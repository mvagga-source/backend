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
import jakarta.persistence.Lob;
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

    // 소속 오디션 회차
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audition_id", nullable = false)
    private AuditionDto audition;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String groupName;

    // 보컬 / 댄서 / 래퍼
    @Column(length = 50)
    private String position;

    private Integer age;

    @Column(length = 100)
    private String hometown;

    //@Column(columnDefinition = "TEXT")
    @Lob
    private String intro;

    // active / eliminated / winner
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
