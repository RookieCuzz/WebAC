package com.cuzz.webac.servers.rocketmq.consumer;

import com.cuzz.webac.mapper.OrderDOMapper;
import com.cuzz.webac.model.doo.OrderDO;
import com.cuzz.webac.servers.WechatService;
import com.cuzz.webac.utils.OrderStatus;
import com.cuzz.webac.utils.PaymentStatus;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "OrderTimeOut", consumerGroup = "OrderTimeOut-consumer-group")
public class OrderTimeOutConsumer implements RocketMQListener<String> {



    @Resource
    WechatService wechatService;
    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        System.out.println("Oi,超时消息向你报道");
        OrderDO orderDO = wechatService.getOrderInfoByOrderNumber(message);
        if (orderDO==null){
            throw new RuntimeException("检测超时订单时,发现db中不存在这订单: "+message);
        }
        PaymentStatus paymentStatus = PaymentStatus.fromCode(orderDO.getPaymentStatus());
        if (paymentStatus==PaymentStatus.UNPAID){
            orderDO.setOrderStatus(OrderStatus.CANCELED.getCode());
            Boolean b = wechatService.updateOrderInfo(orderDO);
            if (b){
                System.out.println("已经修改订单状态");
            }
            System.out.println("订单超时！");
        }else if (paymentStatus==PaymentStatus.PAID){
            System.out.println("订单未超时,订单已经支付！");
        }



    }
}
