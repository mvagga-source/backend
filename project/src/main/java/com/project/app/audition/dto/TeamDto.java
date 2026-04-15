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
@Table(name = "team")
public class TeamDto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_seq_gen")
    @SequenceGenerator(name = "team_seq_gen", sequenceName = "TEAM_SEQ", allocationSize = 1)
	@Column(name = "team_id")
	private Long teamId;
	
	// 소속 오디션 회차
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "audition_id", nullable = false)
	private AuditionDto audition;
	
	// 팀명 ex) "A팀", "B팀"
	@Column(name = "team_name", nullable = false, length = 50)
	private String teamName;
	
	// 팀 대표 이미지 URL
	@Column(name = "team_img_url", length = 500)
	private String teamImgUrl;
	
	@CreationTimestamp
	@Column(updatable = false, name = "create_at")
	private Timestamp createAt;
}
