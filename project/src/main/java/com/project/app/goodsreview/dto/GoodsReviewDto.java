package com.project.app.goodsreview.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList; 

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.app.auth.dto.MemberDto;
import com.project.app.board.dto.BoardDto;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "goods_review")
@ToString(exclude = {"parent", "children"})
public class GoodsReviewDto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grno;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gono")
    private GoodsOrdersDto order;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "idol_no")
    //private IdolDto idol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gno")
    private GoodsDto goods;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private MemberDto member;  // 작성자

    @Lob
    @Column(name = "grcontents")
    private String grcontents; // 댓글내용

    @Column(name = "rating")
    private Double rating; // 별점
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_grno")
    @JsonBackReference // 역참조 방지: 자식 객체에서 부모를 JSON에 포함하지 않음
    private GoodsReviewDto parent; // 부모 리뷰 이게 있으면 '답글'

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference // 정상 참조: 부모 객체에서 자식 목록은 JSON에 포함함
    private List<GoodsReviewDto> children = new ArrayList<>(); // 자식 답글 목록
    
    @ColumnDefault("'n'") // n: 정상, y: 삭제됨
    @Column(name="del_yn", length = 1)
    private String delYn = "n"; // 삭제여부
    
    @Column(name = "gr_img")
    private String grImg; // 리뷰 이미지 파일 경로

    @Column(name = "is_photo", length = 1)		//grImg null체크로도 확인 가능
    @ColumnDefault("'n'")
    private String isPhoto = "n"; // 포토 리뷰 여부 (조회 시 필터링용, 노출하고 싶지 않을 때)
    
    @CreationTimestamp
    @Column(name = "crdt", updatable = false)
    private Timestamp crdt;		//등록일

    @UpdateTimestamp
    @Column(name = "updt")
    private Timestamp updt;		//수정일
    
    //@Embedded
	//private BaseEntity base;	//등록날짜, 등록자, 수정자 등
    
    @Transient
    private long likeCnt;    // 전체 좋아요 개수
    
    @Transient
    private boolean isLiked; // 현재 로그인한 사용자가 눌렀는지 여부
}
