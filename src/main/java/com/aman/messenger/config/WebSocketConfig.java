package com.aman.messenger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.aman.messenger.service.TopicWebSocketHandler;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {


    
    private final TopicWebSocketHandler topicWebSocketHandler;

    public WebSocketConfig(TopicWebSocketHandler topicWebSocketHandler) {
        this.topicWebSocketHandler = topicWebSocketHandler;
    }


    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        registry.addHandler(topicWebSocketHandler,"/ws/chat").setAllowedOrigins("*");
    }


}
