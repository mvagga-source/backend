package com.project.app.audition.dto;

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
@Table(name = "idol_media")
public class IdolMediaDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaId;

    // 어떤 연습생의 미디어인지 연결 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private IdolProfileDto profile;

    // 구분: PHOTO(사진), VIDEO(영상)
    @Column(nullable = false, length = 10)
    private String type;

    // 이미지나 영상의 실제 경로/URL
    @Column(nullable = false, length = 500)
    private String url;

    // 사진 하단에 들어갈 설명 (예: "1 MIN PR", "컨셉 포토 1")
    @Column(length = 200)
    private String description;

    // 화면에 보여줄 순서
    private Integer sortOrder;

    @CreationTimestamp
    @Column(updatable = false)
    private java.sql.Timestamp createAt;
}