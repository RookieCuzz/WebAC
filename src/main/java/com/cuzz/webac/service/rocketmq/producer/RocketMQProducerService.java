package com.cuzz.webac.service.rocketmq.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.springframework.messaging.Message;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RocketMQProducerService {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendMessage(String topic, String message) {

        rocketMQTemplate.convertAndSend(topic, message);
        System.out.println("Sent message: " + message);
    }

    // 发送延时消息
    public void sendDelayMessage(String topic, String message, int delaySeconds) {
        // RocketMQ提供的延时级别 delayLevel(1~18)
        // 1 表示 1秒，2 表示 5秒，3 表示 10秒，等等
        rocketMQTemplate.syncSendDelayTimeSeconds(topic, message, delaySeconds);
        System.out.println("Sent delay message: " + message);
    }


    public void sendTransactionMessage(String orderId, String topic) {
        topic = "OrderNeedShipMessage";

        // 创建消息
        Message<String> order = MessageBuilder.withPayload(orderId).build();

        // 发送事务消息
        TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction(topic, order, topic);

        // 打印发送结果
        log.info("【发送状态】：{}", transactionSendResult.getLocalTransactionState());
        log.info("【事务 ID】：{}", transactionSendResult.getTransactionId());
        log.info("【消息 ID】：{}", transactionSendResult.getMsgId());
        log.info("【发送结果】：{}", transactionSendResult.getSendStatus());
    }

}