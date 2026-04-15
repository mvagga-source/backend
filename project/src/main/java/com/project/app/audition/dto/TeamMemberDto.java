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
@Table(
    name = "team_member",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"team_id", "idol_id"}
        // 같은 팀에 같은 아이돌 중복 소속 방지
    )
)
public class TeamMemberDto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_member_seq_gen")
    @SequenceGenerator(name = "team_member_seq_gen", sequenceName = "TEAM_MEMBER_SEQ", allocationSize = 1)
	@Column(name = "team_member_id")
    private Long teamMemberId;

    // 소속 팀
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamDto team;

    // 소속 아이돌
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idol_id", nullable = false)
    private IdolDto idol;
}
