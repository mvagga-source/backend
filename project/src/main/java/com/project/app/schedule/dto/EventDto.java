package com.project.app.schedule.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
@Table(name = "event")
public class EventDto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq")
	@SequenceGenerator(name = "event_seq", sequenceName = "EVENT_SEQ", allocationSize = 1)
	private Long eno;

	@Column(length = 500, nullable = false)
	private String title;
	
	@Column(length = 10, nullable = false)
	private String startDate;
	
	@Column(length = 10, nullable = false)
	private String endDate;
	
	@Column(length = 1000)
	private String description;
	
	@Column(length = 1, nullable = false)
	@ColumnDefault("'N'") // y:중요일자로 표시	
	private String highlightFlag = "N";
	
	@Column(length = 1, nullable = false)
	@ColumnDefault("'N'") // y:삭제, n:정상
	private String deletedFlag = "N";	
	
	@CreationTimestamp
    private LocalDateTime createdAt;	
	
	@Column
	private LocalDateTime deletedAt;	
	
}
