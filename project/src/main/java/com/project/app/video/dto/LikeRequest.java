package com.project.app.video.dto;

import com.project.app.auth.dto.MemberDto;

import lombok.Data;

@Data
public class LikeRequest {
	private String memberId;
    private Long videoId;
}
