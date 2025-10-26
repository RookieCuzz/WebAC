package com.cuzz.webac.service.rocketmq.consumer;


import com.alipay.remoting.exception.RemotingException;
import com.cuzz.common.rookiepay.RookiePaySuccessMessage;
import com.cuzz.webac.broker.SpringClient;
import com.cuzz.webac.caches.Caches;
import com.cuzz.webac.model.vo.QRCodeVO;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

//@Service
@RocketMQMessageListener(topic = "OrderNeedShipMessage", consumerGroup = "OrderNeedProcess-GameServerConsumer-group")
public class OrderPaySuccessMessageGameConsumer implements RocketMQListener<String> {


    @Resource
    SpringClient springClient;

    @Resource
    Caches orderCaches;

    @Override
    public void onMessage(String orderNumber) {
        //通知游戏服务器集群 提醒玩家发货成功
        RookiePaySuccessMessage paySuccessMessage = new RookiePaySuccessMessage();

        QRCodeVO qrCodeVO = orderCaches.getCacheOrders().get(orderNumber);
        String buyer = qrCodeVO.getBuyer();
        paySuccessMessage.setPlayer(buyer);
        paySuccessMessage.setOrderId(orderNumber);
        try {
            springClient.getBrokerClient().invokeSync(paySuccessMessage);

        } catch (RemotingException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("收到事务消息:"+"订单号是 "+ orderNumber
        );

    }
}
