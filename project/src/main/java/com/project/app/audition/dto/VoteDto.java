package com.project.app.audition.dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.project.app.auth.dto.MemberDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(
    name = "vote",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"member_id", "audition_id", "vote_date"}
        // 같은 회원이 같은 회차에 하루 1번만 투표 가능
    )
)
public class VoteDto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vote_seq_gen")
    @SequenceGenerator(name = "vote_seq_gen", sequenceName = "VOTE_SEQ", allocationSize = 1)
	@Column(name = "vote_id")
    private Long voteId;

    // 투표한 회원 (MemberDto의 PK인 String id 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private MemberDto member;

    // 어느 회차 투표인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audition_id", nullable = false)
    private AuditionDto audition;

    // 투표한 날짜 (중복 방지 기준, 자동 설정)
    @Column(nullable = false, updatable = false, name = "vote_date")
    private LocalDate voteDate;

    // 선택한 아이돌 목록 (최대 7명)
    // Vote 저장/삭제 시 VoteDetail도 함께 처리
    @Builder.Default
    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteDetailDto> voteDetails = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false, name = "create_at")
    private Timestamp createAt;

    // 투표일 자동 설정 (insert 직전 실행)
    @PrePersist
    public void prePersist() {
        this.voteDate = LocalDate.now();
    }
}
