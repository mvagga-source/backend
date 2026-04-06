package com.project.app.notice.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.app.auth.dto.MemberDto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@DynamicInsert
@SQLDelete(sql = "UPDATE notice SET del_yn = 'y' WHERE nno = ?")
@SQLRestriction("del_yn = 'n'")
@Table(name = "notice")
public class NoticeDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nno;		//pk

    @Column(length = 200)
    private String ntitle;	//공지제목

    @Lob
    private String ncontent;	//공지내용

    // 공지사항 작성자 (관리자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private MemberDto member;

    // 노출 시작 일시
    @Column(name="start_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;	// 노출 시작 일시

    // 노출 종료 일시
    @Column(name="end_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;	// 노출 종료 일시

    // 중요 공지 여부 (상단 고정 등을 위해 사용 가능)
    @ColumnDefault("'n'")
    @Column(name="is_pinned", length = 1)	// 중요 공지 여부: y(고정), n(고정없음)
    private String isPinned;		//팝업형태라 사용안할 수 있음

    @ColumnDefault("'n'") // n: 정상, y: 삭제됨
    @Column(name="del_yn", length = 1)
    private String delYn = "n"; // 삭제여부
    
    @ColumnDefault("0")
    private Integer nhit;	//조회수

    @CreationTimestamp
    @Column(name = "crdt", updatable = false)
    private Timestamp crdt;		//등록일
    
    @UpdateTimestamp
    @Column(name = "updt")
    private Timestamp updt;		//수정일

    @Column(name="nfile_name", length = 200)
    private String nfileName;		//팝업형태라 사용안할 수 있음
    
    // 파일 첨부가 필요하다면 추가
    @Lob
    private String nfile;		//파일(혹시 파일첨부가 필요하다면 추가)	//팝업형태라 사용안할 수 있음
}