package com.project.app.boardlike.dto;

import java.sql.Timestamp;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "board_like")
public class BoardLikeDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lbno;

    // 추천한 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private MemberDto member;

    // 추천받은 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bno")
    private BoardDto board;

    // 추천(1) 또는 비추천(-1) 구분용 (필요 시 사용)
    @ColumnDefault("1")
    @Column(name="is_like")
    private Integer isLike;

    @CreationTimestamp
    private Timestamp ldate;
}