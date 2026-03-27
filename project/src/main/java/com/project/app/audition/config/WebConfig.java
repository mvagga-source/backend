package com.project.app.audition.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // 리액트 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // ✅ 이 줄이 핵심입니다! (Credentials 허용)
    }
    
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 브라우저에서 /images/** 로 시작하는 주소로 요청이 오면
        registry.addResourceHandler("/images/**")
                // 2. 실제 컴퓨터의 C:/upload/ 폴더에서 파일을 찾으라는 설정
                .addResourceLocations("file:///C:/upload/"); 
    }
    
    
}