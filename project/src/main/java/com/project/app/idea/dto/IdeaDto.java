package com.project.app.idea.dto;

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
@Table(name = "idea")
public class IdeaDto {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ideano;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private MemberDto member; // 작성자
	
	@Column(length = 100, nullable = false)
    private String ideatitle;      // 제목
    
	@Lob
	private String ideacontent;    // 구체적인 내용 (가이드 기반)
    
    @CreationTimestamp
    @Column(name = "crdt", updatable = false)
    private Timestamp crdt;		//등록일
    
    @UpdateTimestamp
    @Column(name = "updt")
    private Timestamp updt;		//수정일
    
    // API 응답 시 시간을 "n분 전" 등으로 변환하기 위한 필드
    private String displayTime;
}