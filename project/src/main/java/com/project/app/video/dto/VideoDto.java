package com.project.app.video.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.project.app.audition.dto.IdolProfileDto;
import com.project.app.auth.dto.MemberDto;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_seq")
	@SequenceGenerator(name = "video_seq", sequenceName = "VIDEO_SEQ", allocationSize = 1)
	private Long id;
	
	// 노래제목(변경필요)
	private String title;

	@Column
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
	
    // 아이돌 연결 (N:1)	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profileId")
    private IdolProfileDto idol_profile;
	
	@Column(length = 1, nullable = false)
	@ColumnDefault("'N'") // y:삭제, n:정상
	private String deletedFlag = "N";
	
	@CreationTimestamp
    private LocalDateTime createdAt;
	
	@Column
	private LocalDateTime deletedAt;
}

