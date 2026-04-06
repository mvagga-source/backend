package com.project.app.video.dto;

import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;

@Data
public class VideoRequestParams {
	private Integer page = 1;
	private Integer size = 10;
	private String sortType = "LATEST";
	private String search;	
	private String searchType;	
}