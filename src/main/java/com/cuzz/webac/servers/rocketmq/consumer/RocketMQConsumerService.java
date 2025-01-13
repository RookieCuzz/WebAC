package com.cuzz.webac.servers.rocketmq.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

//@Service
@RocketMQMessageListener(topic = "OrderTimeOut", consumerGroup = "OrderTimeOut-consumer-group")
public class RocketMQConsumerService implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        System.out.println("Oi,超时消息向你报道");
    }
}
