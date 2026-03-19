package com.project.app.calendar.dto;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "event")
public class EventDto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eno;

	@Column(length = 500, nullable = false)
	private String title;
	@Column(length = 10, nullable = false)
	private String startDate;
	@Column(length = 10, nullable = false)
	private String endDate;
	@Column(length = 1000)
	private String description;
	@CreationTimestamp
	private Timestamp cdate;
	@UpdateTimestamp
	private Timestamp udate;

}
