package com.project.app.audition.dto;

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

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "support_project")
public class SupportProjectDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supportId;

    private Long mediumId; // 선택한 광고 매체 고유번호

    @Column(nullable = false, length = 200)
    private String title; // 광고 제목

    @Column(length = 50)
    private String category; // 지하철, 버스, 전광판

    @Column(name = "sub_type", length = 50)
    private String subType; // cm보드, 조명광고 등

    @Column(name = "unit_price")
    private Long unitPrice; // 매체 특징/단가

    @Column(name = "location_dtl", length = 200)
    private String locationDtl; // 상세 위치 정보

    @Column(name = "goal_price")
    private Long goalPrice;

    @Column(name = "curr_price")
    private Long currPrice;

    private Integer participants;

    // 모금중, 심사, 집행, 완료 (proj_status)
    @Column(name = "proj_status", length = 20)
    private String projStatus;

    private java.time.LocalDate startDate;

    private java.time.LocalDate endDate;

    @Column(name = "atstartdate")
    private java.time.LocalDate atStartDate; // 실제 광고 예정일
}