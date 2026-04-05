package com.project.app.video.dto;

import lombok.Data;

@Data
public class AVideoRequestParams {
	private Long id;	
	private Long idol_profile;
	private String title;
	private String url;	
	private String status = "1";	

}
   