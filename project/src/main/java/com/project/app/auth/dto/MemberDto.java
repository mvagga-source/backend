package com.project.app.auth.dto;

import java.sql.Timestamp;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Entity // JPA설정 - 객체형태로 오라클DB에 테이블 생성
@Table(name = "member")
public class MemberDto {
	
	@Id // primary key 설정
	@Column(length = 50)
	private String id;
	
	@Column(length = 200, nullable = false)
	private String pw;
	
	@Column(length = 50)
	private String nickname;
	
	//@Column(length = 13)
	//private String phone;
	
	@Column(length = 50)
	private String email;
	
	@CreationTimestamp
	@Column(updatable = false, name="create_at")
	private Timestamp createAt;
}
