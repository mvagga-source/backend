package com.project.app.mypage.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MyVoteResponse {
	private LocalDateTime voteDate;
	private Long voteId;
	private Long idolId;
	private String idolImgUrl;
}
