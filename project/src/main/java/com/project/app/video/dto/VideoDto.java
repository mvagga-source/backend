package com.project.app.video.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "video")
public class VideoDto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 연습생 이름(변경필요)
	private String name;
	
	// 노래제목(변경필요)
	private String title;
	
	@Column(length = 1, nullable = false)
	@ColumnDefault("1") // 1:합격, 0:탈락
	private String status;
	
	private String url;
	
    // 좋아요
	@ColumnDefault("0")
    private int likeCount;
    
    // 조회수
	@ColumnDefault("0")	
    private int viewCount;	
	
	// 인기순
	@ColumnDefault("0")	
    private double popCount;
	
	@CreationTimestamp
    private LocalDateTime createdAt;	
}
