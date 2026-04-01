package com.project.app.audition.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdolResponseDto {

	private Long idolId;
	private Long idolProfileId;  // IdolProfile 연결 후 이름 등 조회
    private String status;
    private Long votes;    // vote_detail 집계값 (실시간 득표수)
    
    private String name; // idol_profile.name (참가자 이름)
    private String mainImgUrl;  // idol_profile.main_img_url (프로필 이미지 파일명)
}
