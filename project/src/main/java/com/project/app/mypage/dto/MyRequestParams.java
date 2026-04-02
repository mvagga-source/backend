package com.project.app.mypage.dto;

import lombok.Data;

@Data
public class MyRequestParams {
	private String memberId;
	private Integer page = 0;
	private Integer size = 10;
	private String startDate;
	private String endDate;
}
