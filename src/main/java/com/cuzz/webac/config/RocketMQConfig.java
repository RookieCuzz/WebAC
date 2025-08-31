package com.cuzz.webac.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfig {

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
