package com.project.app.admin.dto.schedule;

import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;

@Data
public class ScheduleRequestParams {
	private Integer page = 1;
	private Integer size = 10;
	private String startDate;
	private String endDate;
	private String sortType = "LATEST";
	private String search;	
	private String searchType;	
}