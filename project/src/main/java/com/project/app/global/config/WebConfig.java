package com.project.app.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadBaseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 대표 프로필 이미지
        // 호출: /profile/파일명.jpg
        registry.addResourceHandler("/profile/**")
                .addResourceLocations("file:///C:/upload/action profile/");

        // 상세 프로필 사진
        // 호출: /images/파일명.jpg
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///C:/upload/action profile/images/");

        // 팀 대표 이미지
        // 호출: /teamImages/파일명.jpg
        registry.addResourceHandler("/teamImages/**")
        		.addResourceLocations("file:///" + uploadBaseDir + "action profile/teamImages/");

        // 전체 업로드 폴더 직접 접근 (MyVote 등)
        // 호출: /upload/파일명
        registry.addResourceHandler("/upload/**")
        		.addResourceLocations("file:///C:/upload/action profile/", 
	                                  "file:///C:/upload/action profile/images/",
	                                  "file:///C:/upload/");
	        
	    }
}
