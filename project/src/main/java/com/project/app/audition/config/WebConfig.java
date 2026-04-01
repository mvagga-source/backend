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
    	
        // 리액트 호출 주소: /profile/파일명.jpg
        registry.addResourceHandler("/profile/**")
                .addResourceLocations("file:///C:/upload/");

        
        // 리액트 호출 주소: /images/파일명.jpg
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///C:/upload/images/");
    }
    
    
}