package com.project.app.comment.dto;

import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.app.board.dto.Board;
import com.project.app.user.dto.Member;

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
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cno;

	@Lob
	private String ccontent;

	@ManyToOne
	@JoinColumn(name="bno")
	@JsonIgnore
	private Board board;

	@ManyToOne
	@JoinColumn(name="id")
	private Member member;

	//@CreationTimestamp
	@UpdateTimestamp
	private Timestamp cdate;
}
