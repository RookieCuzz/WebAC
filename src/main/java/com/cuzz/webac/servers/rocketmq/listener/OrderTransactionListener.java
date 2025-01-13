package com.cuzz.webac.servers.rocketmq.listener;

import com.alibaba.fastjson.JSON;
import com.cuzz.webac.dao.OrderDao;
import com.cuzz.webac.model.doo.OrderDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
@Slf4j
@RocketMQTransactionListener(rocketMQTemplateBeanName = "rocketMQTemplate")
public class OrderTransactionListener implements RocketMQLocalTransactionListener {

    @Resource
    OrderDao orderDao;

    // 执行本地事务
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        try {
            String orderNumber = new String((byte[]) message.getPayload());
            log.error("事务消息为XXX:{}", orderNumber);

            Boolean result = orderDao.updateOrderToProcessing(orderNumber);

            if (result) {
                log.info("订单更新为支付完成成功 :{}", result);
                return RocketMQLocalTransactionState.COMMIT;
            }
            log.info("订单更新为支付完成失败 :{}", result);
        } catch (Exception e) {
            log.error("发送事务消息异常:{}", e.getMessage());
            return RocketMQLocalTransactionState.UNKNOWN;
        }
        return RocketMQLocalTransactionState.ROLLBACK;

    }

    // 回查本地事务状态
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        try {
            String orderNumber = new String((byte[]) message.getPayload());
            log.error("事务消息为XXX:{}", orderNumber);
            // 查询订单状态以确认事务是否成功
            Boolean result = orderDao.checkOrderIsPayAndProcessing((String) message.getPayload());

            if (result) {
                log.info("事务回查成功，订单状态已更新: {}", orderNumber);
                return RocketMQLocalTransactionState.COMMIT; // 提交事务
            } else {
                log.info("事务回查失败，订单状态未更新: {}", orderNumber);
                return RocketMQLocalTransactionState.ROLLBACK; // 回滚事务
            }
        } catch (Exception e) {
            log.error("事务回查异常: {}", e.getMessage());
            return RocketMQLocalTransactionState.UNKNOWN; // 状态未知
        }
    }
    // 回查本地事务
}
