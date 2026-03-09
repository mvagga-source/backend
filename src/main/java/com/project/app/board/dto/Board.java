package com.project.app.board.dto;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.app.comment.dto.Comment;
import com.project.app.user.dto.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
public class Board {
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="boardDto_seq_generater")	//oracle 시퀀스
	private Long bno;

	@Column(nullable=false, length=2000)
	private String btitle;

	@Lob			//대용량 문자열 - 오라클의 CLOB
	private String bcontent;

	//OneToMany(여러사람이 한 게시글 등록), ManyToMany(여러명회원이 여러개 글쓰기 가능), ManyToOne(한명의 회원은 여러개의 게시글 작성가능)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id")
	private Member member;

//	@ColumnDefault("0")
//	private Long bgroup;
//
//	@ColumnDefault("0")
//	private Long bstep;
//
//	@ColumnDefault("0")
//	private Long bindent;

	@ColumnDefault("0")
	private Integer bhit;

	@Column(length=1000)
	private String bfile;

	@CreationTimestamp
	private Timestamp bdate;

	//하단댓글 리스트 추가
	// 1개의 게시글은 여러개의 댓글을 가질 수 있음.
	// mappedBy : 연관관계의 주인이 아니다.(FK가 아님.)
	@OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@JsonIgnoreProperties({"board"})		//무한루프방지 > 무한루프 걸리는 상황이 CommentDto에 boarddto를 다시 불러옴(CommentDto에서 끊어 주는게 좋음)
	@OrderBy("cno desc")					//cno역순정렬
	private List<Comment> comment;

}
