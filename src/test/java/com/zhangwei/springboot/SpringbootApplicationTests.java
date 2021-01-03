package com.zhangwei.springboot;

import com.zhangwei.springboot.jms.JmsComponent;
import com.zhangwei.springboot.jms.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class SpringbootApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    JmsComponent jmsComponent;
    @Test
    void testJms(){
        Message msg=new Message();
        msg.setContent("hello jms");
        msg.setDate(new Date());
        jmsComponent.send(msg);
    }

}
