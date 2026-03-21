package com.project.app.video.dto;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.project.app.auth.dto.MemberDto;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(
		name= "video_like",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"member_id","video_id"})
		}
)
public class LikeDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
	private MemberDto member;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private VideoDto video;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}
