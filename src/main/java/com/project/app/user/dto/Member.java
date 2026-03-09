package com.project.app.user.dto;

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
//@Table(name = "member")
public class Member {

	@Id // primary key 설정
	@Column(length = 50)
	private String id;

	@Column(length = 30, nullable = false)
	private String pw;

	@Column(length = 100, nullable = false)
	private String name;

	@Column(length = 13)
	private String phone;

	@Column(length = 50)
	private String email;

	@ColumnDefault("'남자'")
	@Column(length = 6)
	private String gender;

	@Column(length = 100)
	private String hobby;

	@CreationTimestamp
	private Timestamp mdate;

}
