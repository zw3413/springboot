package com.zhangwei.springboot.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

@Component
public class JmsComponent {

    @Autowired
    JmsMessagingTemplate messagingTemplate; //Spring提供的JMS消息发送模板

    @Autowired
    Queue queue;

    public void send(Message msg){
        messagingTemplate.convertAndSend(queue,msg);
    }

    //@JmsListener(destination = "amq") //注册为消息消费者，定于amq消息队列
    public void receive(Message msg){
        System.out.println("receive:"+msg);
    }
}
