package com.cuzz.webac.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.proxy.endpoints:103.205.253.165:15081}")
    private String proxyEndpoints;

    @Value("${rocketmq.producer.group:shop_group}")
    private String producerGroup;

    @Value("${rocketmq.consumer.group:shop-group}")
    private String consumerGroup;

    // gRPC Proxy 连接配置
    // 注意：RocketMQ 5.x 的 Spring Boot Starter 会自动处理 gRPC 连接
    // 这里主要是为了提供配置参数的注入

//    // 创建事务生产者，绑定到特定的 TransactionListener
//    @Bean
//    public TransactionMQProducer transactionMQProducer() {
//        TransactionMQProducer producer = new TransactionMQProducer("order-transaction-group");
//
//        // 配置事务监听器，这里可以选择你需要的监听器
//        producer.setTransactionListener((TransactionListener) new OrderTransactionListener());  // 绑定 OrderTransactionListener
//
//        // 启动生产者
//        try {
//            producer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return producer;
//    }
}
