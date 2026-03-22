package com.project.app.bookmark.dto;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "bookmark")
public class BookmarkDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(length = 30, nullable = false)
    private String memberId;
	
	@Enumerated(EnumType.STRING)
	private PageType pageType;

	private Long pageId;

    @CreationTimestamp
    private LocalDateTime createdAt;
	
}
