package com.cuzz.webac.service.rocketmq.listener;

import com.cuzz.webac.dao.OrderDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object topic) {
        log.info("进入 executeLocalTransaction 方法");
        log.info("接收到的消息主题: {}", topic);

        if (topic.toString().equalsIgnoreCase("OrderNeedShipMessage")) {
            try {
                String orderNumber = new String((byte[]) message.getPayload());
                log.info("事务消息内容: {}", orderNumber);

                Boolean result = orderDao.updateOrderToProcessing(orderNumber);
                if (result) {
                    log.info("订单更新成功，订单编号: {}", orderNumber);
                    return RocketMQLocalTransactionState.COMMIT;
                } else {
                    log.info("订单更新失败，订单编号: {}", orderNumber);
                    return RocketMQLocalTransactionState.ROLLBACK;
                }
            } catch (Exception e) {
                log.error("事务消息处理异常", e);
                return RocketMQLocalTransactionState.UNKNOWN;
            }
        }
        log.warn("未知主题: {}", topic);
        return RocketMQLocalTransactionState.ROLLBACK;
    }

    // 回查本地事务状态
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        try {
            String orderNumber = new String((byte[]) message.getPayload());
            log.error("回查的事务消息为XXX:{}", orderNumber);
            // 查询订单状态以确认事务是否成功
            Boolean result = orderDao.checkOrderIsPayAndProcessing(orderNumber);


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
