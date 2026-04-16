package com.project.app.audition.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table( name = "vote_detail" )
// 중복 2중안전장치(VoteServiceImpl → 중복방지logic존재)
//@Table(
//    name = "vote_detail",
//    uniqueConstraints = @UniqueConstraint(
//        columnNames = {"vote_id", "idol_id"}
//        // 같은 투표에서 동일 아이돌 중복 선택 방지
//    )
//)

public class VoteDetailDto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vote_detail_seq_gen")
    @SequenceGenerator(name = "vote_detail_seq_gen", sequenceName = "VOTE_DETAIL_SEQ", allocationSize = 1)
	@Column(name = "vote_detail_id")
	private Long voteDetailId;
	
	// 소속 투표 묶음
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vote_id", nullable = false)
	private VoteDto vote;
	
	// 선택된 아이돌
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idol_id", nullable = false)
	private IdolDto idol;
}
