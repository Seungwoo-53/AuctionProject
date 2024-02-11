package com.auction.WebAuction.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker  // Websocket 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer  {
    @Autowired
    private WebSocketHandler webSocketHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 메시지 브로커를 "/topic"으로 설정
        config.setApplicationDestinationPrefixes("/app"); // 클라이언트에서 메시지를 보낼 때의 prefix 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS(); // 클라이언트에서 연결할 웹 소켓 엔드포인트 설정
    }
}