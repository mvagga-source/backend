package com.project.app.report.dto;

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
@Table(name="report")
public class ReportDto {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long repono;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private MemberDto member; // 신고자
	
	@Column(name="report_type", length = 25)
    private String reportType;   // 신고 유형 (욕설, 도배, 부적절한 콘텐츠 등)
    
	@Column(name="target_type")
    private String targetType;   // 신고 대상 구분 (POST, COMMENT, USER 등)
    
	@Column(name="target_id")
    private Long targetId;       // 신고 대상의 고유 PK
    
	@Lob
    private String reason;       // 상세 신고 사유
    
    @ColumnDefault("'신고대기'")
    @Column(length = 25)
    private String status;       // 처리 상태 (신고대기, 처리완료, 신고반려)
    
    @CreationTimestamp
    @Column(name = "crdt", updatable = false)
    private Timestamp crdt;		//등록일
    
    @UpdateTimestamp
    @Column(name = "updt")
    private Timestamp updt;		//수정일
    
}