package com.zhangwei.springboot.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker //开启websocket消息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic","/queue"); //设置消息代理的前缀,用于前端订阅接收服务端消息
        config.setApplicationDestinationPrefixes("/app"); //配置需要被@MessageMapping注解方法处理的消息,用于前端发送消息
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/chat").withSockJS(); //定义一个前缀为“/char”的endPoint,并开启sockjs支持，前端使用此endPoint创建websocket连接
    }
}
