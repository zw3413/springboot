package com.zhangwei.springboot.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class GreetController {

    @Autowired
    SimpMessagingTemplate messagingTemplate; //Spring提供的简单消息发送模板

    @MessageMapping("/hello") // 接收通过/app/hello路径发送的消息
    @SendTo("/topic/greetings") //在注解方法中对消息进行处理后，再转发给/topic路径的消息代理broker，进行广播
    public Message greeting(Message message) throws Exception{
        return message;
    }

    @MessageMapping("/chat")
    public void chat(Principal principal,Chat chat) throws Exception{
        String from = principal.getName();
        chat.setFrom(from);
        messagingTemplate.convertAndSendToUser(chat.getTo(),"/queue/chat",chat);
    }
}

