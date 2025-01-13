package com.cuzz.webac.servers.rocketmq.consumer;


import com.alipay.remoting.exception.RemotingException;
import com.cuzz.common.rookiepay.RookiePaySuccessMessage;
import com.cuzz.webac.broker.SpringClient;
import com.cuzz.webac.caches.OrderCaches;
import com.cuzz.webac.model.vo.QRCodeVO;
import com.cuzz.webac.servers.WechatService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

//@Service
//@RocketMQMessageListener(topic = "OrderNeedProcessMessage", consumerGroup = "OrderNeedProcess-DeliverServerConsumer-group")
public class OrderDeliverConsumer implements RocketMQListener<String> {


    @Resource
    SpringClient springClient;

    @Resource
    OrderCaches orderCaches;

    @Resource
    WechatService wechatService;

    @Override
    public void onMessage(String orderNumber) {
        //通知游戏服务器集群 发货
        QRCodeVO qrCodeVO = orderCaches.getCacheOrders().get(orderNumber);
        //执行发货逻辑


        System.out.println("收到事务消息:"+"订单号是 "+ orderNumber
        );

    }
}
