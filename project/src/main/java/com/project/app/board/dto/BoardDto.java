package com.project.app.board.dto;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.app.auth.dto.MemberDto;
import com.project.app.boardcomment.dto.BoardCommentDto;
import com.project.app.boardlike.dto.BoardLikeDto;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*@SequenceGenerator(
		name="boardDto_seq_generater"	//generator 이름
		,sequenceName = "boardDto_seq"	//오라클 테이블에서 시퀀스이름
		,initialValue = 1				//시작번호
		,allocationSize = 1				//메모리 할당범위
)*/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity // JPA설정 - 객체형태로 오라클DB에 테이블 생성
@SQLDelete(sql = "UPDATE board SET del_yn = 'y' WHERE bno = ?")
//@SQLRestriction("del_yn = 'n'")		//관리자에서는 조회 가능할 수 있음
@Table(name = "board")
public class BoardDto {
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="boardDto_seq_generater")	//oracle 시퀀스
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bno;

	@Column(nullable=false, length=2000)
	private String btitle;

	@Lob			//대용량 문자열 - 오라클의 CLOB
	private String bcontent;

	//OneToMany(여러사람이 한 게시글 등록), ManyToMany(여러명회원이 여러개 글쓰기 가능), ManyToOne(한명의 회원은 여러개의 게시글 작성가능)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id")
	private MemberDto member;

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
	
	// 해당 게시글의 전체 추천 리스트 (삭제 시 함께 삭제)
    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties({"board"})
    private List<BoardLikeDto> likes;

	//하단댓글 리스트 추가
	// 1개의 게시글은 여러개의 댓글을 가질 수 있음.
	// mappedBy : 연관관계의 주인이 아니다.(FK가 아님.)
	@OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@JsonIgnoreProperties({"board"})		//무한루프방지 > 무한루프 걸리는 상황이 CommentDto에 boarddto를 다시 불러옴(CommentDto에서 끊어 주는게 좋음)
	@OrderBy("cno desc")					//cno역순정렬
	private List<BoardCommentDto> comment;
	
	@ColumnDefault("'n'") // n: 정상, y: 삭제됨
	@Column(name = "del_yn", length = 1)
    private String delYn = "n";
	
	@ColumnDefault("'n'") // n: 정상, y: 신고처리됨
	@Column(name = "report_yn", length = 1)		//신고처리삭제여부
	private String reportYn = "n";

	//리스트 출력할때 느려질 수 있음
	@Formula("(SELECT b.bno FROM board b WHERE b.bno < bno and b.report_yn = 'n' ORDER BY b.bno DESC FETCH FIRST 1 ROWS ONLY)")
    private Long prevBno;

    @Formula("(SELECT b.btitle FROM board b WHERE b.bno < bno and b.report_yn = 'n' ORDER BY b.bno DESC FETCH FIRST 1 ROWS ONLY)")
    private String prevTitle;

    @Formula("(SELECT b.bno FROM board b WHERE b.bno > bno and b.report_yn = 'n' ORDER BY b.bno ASC FETCH FIRST 1 ROWS ONLY)")
    private Long nextBno;

    @Formula("(SELECT b.btitle FROM board b WHERE b.bno > bno and b.report_yn = 'n' ORDER BY b.bno ASC FETCH FIRST 1 ROWS ONLY)")
    private String nextTitle;
    
    // 추천수
    @Formula("(SELECT count(1) FROM board_like bl WHERE bl.bno = bno AND bl.is_like = 1)")
    private Integer likeCount;

    // 비추천수
    @Formula("(SELECT count(1) FROM board_like bl WHERE bl.bno = bno AND bl.is_like = -1)")
    private Integer dislikeCount;
}
