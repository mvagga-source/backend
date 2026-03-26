package com.project.app.audition.dto;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = "idol_profile")
public class IdolProfileDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    // 이름
    @Column(nullable = false, length = 100)
    private String name;

    // 영문이름
    @Column(name = "name_en", length = 100)
    private String nameEn;

    // 생년월일
    private java.time.LocalDate birth;

    // 키
    private Integer height;

    // 나이
    private Integer age;

    
    @Column(length = 4)
    private String mbti;

    @Column(length = 100)
    private String hobby;

    // 키워드
    @Column(length = 100)
    private String keyword;


 // 상단에 크게 들어갈 메인 프로필 이미지 1장
    @Column(name = "main_img_url", length = 500)
    private String mainImgUrl;

   
    @Lob
    private String content; // 활동 내용 상세

    @CreationTimestamp
    @Column(updatable = false, name = "create_at")
    private java.sql.Timestamp createAt;
}