package com.project.app.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	//실무에서는 db는 느리고 redis와 같이 사용

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커가 해당 주소를 구독 중인 클라이언트에게 메시지 전달
        // /sub/notification, /sub/chat 등
        config.enableSimpleBroker("/sub"); 
        
        // 클라이언트가 메시지를 보낼 때 붙이는 접두사
        // ex) /pub/send-notification
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp") // 웹소켓 연결 지점
                .setAllowedOriginPatterns("*") // CORS 허용 (실무에선 특정 도메인 지정)
                .withSockJS(); // SockJS 지원 (브라우저 호환성 및 보안)
    }
}