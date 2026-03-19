package com.project.app.boardcomment.dto;

import java.sql.Timestamp;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;

import jakarta.persistence.Entity;
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

@Data				//getter/setter
@AllArgsConstructor	//전체생성자
@NoArgsConstructor	//기본생성자
@Builder			//부분생성자
@Entity
@Table(name="board_comment")
public class BoardCommentDto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cno;

	@Lob
	private String ccontent;

	@ManyToOne
	@JoinColumn(name="bno")
	@JsonIgnore
	private BoardDto board;

	@ManyToOne
	@JoinColumn(name="id")
	private MemberDto member;

	@ColumnDefault("0")		//같은 원글에 달린 모든 댓글과 답글을 하나의 그룹으로 묶어주는 역할
	private Long cgroup;

	@ColumnDefault("0")		//같은 그룹 내에서 어떤 순서로 보여줄지를 결정하는 번호
	private Long cstep;

	@ColumnDefault("0")	//들여쓰기 정도
	private Long cindent;

	//@CreationTimestamp
	@UpdateTimestamp
	private Timestamp cdate;
}
