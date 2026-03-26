package com.project.app.qna.dto;

import java.sql.Timestamp;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;
import com.project.app.boardcomment.dto.BoardCommentDto;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "qna")
public class QnaDto {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qno;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private MemberDto member; // 문의 작성자
	
	@Column(length = 100, nullable = false)
    private String qtitle;     // 문의 제목

    @Lob
    private String qcontent;   // 문의 상세 내용

    @Lob
    @Column(name="answer_content")
    private String answerContent;    // 관리자 답변 내용

    @ColumnDefault("'답변대기'")
    @Column(length = 20)
    private String status;    // 답변 상태 (답변대기, 답변완료)
    
    @ColumnDefault("'n'") // n: 정상, y: 삭제됨
    @Column(name="del_yn", length = 1)
    private String delYn = "n"; // 삭제여부
    
    @CreationTimestamp
    @Column(name = "crdt", updatable = false)
    private Timestamp crdt;		//등록일
    
    @UpdateTimestamp
    @Column(name = "updt")
    private Timestamp updt;		//수정일
}