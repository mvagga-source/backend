package com.project.app.goods.dto;

import java.sql.Timestamp;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.app.audition.dto.AuditionDto;
import com.project.app.audition.dto.IdolDto;
import com.project.app.audition.dto.IdolProfileDto;
import com.project.app.audition.dto.TeamDto;
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

@Data				//getter/setter
@AllArgsConstructor	//전체생성자
@NoArgsConstructor	//기본생성자
@Builder			//부분생성자
@Entity
@Table(name="goods")	//굿즈상품
public class GoodsDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gno;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "idol_key") 
    //private Idol idol;     // 아이돌 pk관계필요

    // 아이돌 연결 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profileId")
    private IdolProfileDto idol;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private MemberDto member;  // 판매자
    
    @Column(length = 100)
    private String gname;		// 상품명
    
    @Lob
    private String gcontent; // 설명
    
    @ColumnDefault("0")
    @Column(name="price")
    private Long price;       // 가격
    
    @Column(name="gdeliv_price")
    private Long gdelPrice;  // 배송료
    
    @Column(name="gdeliv_type")
    private String gdelType; // 배송타입(택배사)
    
    @Lob
    @Column(name="gdeliv_addr")
    private String gdelivAddr; // 배송시작주소
    
    @Lob
    @Column(name="gdeliv_addr_return")
    private String gdelivAddrReturn; // 배송반품주소
    
    @Lob
    @Column(name="gdeliv_addr_return_detail")
    private String gdelivAddrReturnDetail; // 배송반품상세주소
    
    @Lob
    @Column(name="gimg")
    private String gimg;  // 굿즈이미지
    
    @ColumnDefault("0")
    @Column(name="stock_cnt")
    private Long stockCnt;    // 재고수량
    
    @Column(name = "status", length = 25)
    private String status; // (판매중/품절/판매중지)
    
    @ColumnDefault("'n'") // n: 정상, y: 삭제됨
    @Column(name="del_yn", length = 1)
    private String delYn = "n"; // 삭제여부
    
    @Column(name = "is_banner", length = 1)
    @ColumnDefault("'n'")
    private String isBanner = "n"; // 배너 노출 여부 (y: 노출, n: 미노출)
    
    @Column(name = "banner_sort")
    @ColumnDefault("0")
    private Integer bannerSort; // 배너 노출 우선순위 (숫자가 낮을수록 앞에 노출)
    
    @CreationTimestamp
    @Column(name = "crdt", updatable = false)
    private Timestamp crdt;		//등록일
    
    @UpdateTimestamp
    @Column(name = "updt")
    private Timestamp updt;		//수정일
	
    //@Embedded
	//private BaseEntity base;	//등록날짜, 등록자, 수정자 등
}
