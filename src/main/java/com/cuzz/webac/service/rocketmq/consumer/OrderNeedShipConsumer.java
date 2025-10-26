package com.cuzz.webac.service.rocketmq.consumer;


import com.cuzz.webac.bot.bot.BotAPI;
import com.cuzz.webac.bot.objects.message.GeneralMessage;
import com.cuzz.webac.broker.SpringClient;
import com.cuzz.webac.caches.Caches;
import com.cuzz.webac.dao.MongoDaoImp;
import com.cuzz.webac.model.doo.OrderDO;
import com.cuzz.webac.service.OrderWechatService;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

//@Service
@RocketMQMessageListener(topic = "OrderNeedShipMessage", consumerGroup = "OrderNeedProcess-GameServerConsumer-group")
public class OrderNeedShipConsumer implements RocketMQListener<String> {


    @Resource
    SpringClient springClient;

    @Resource
    Caches orderCaches;

    @Resource
    MongoDaoImp shipDaoImp;

    @Resource
    OrderWechatService orderWechatService;

    @Override
    public void onMessage(String orderNumber) {
        //通知游戏服务器集群 提醒玩家发货成功
//        RookiePaySuccessMessage paySuccessMessage = new RookiePaySuccessMessage();
//
//        QRCodeVO qrCodeVO = orderCaches.getCacheOrders().get(orderNumber);
//        String buyer = qrCodeVO.getBuyer();
//        paySuccessMessage.setPlayer(buyer);
//        paySuccessMessage.setOrderId(orderNumber);
//        try {
//            springClient.getBrokerClient().invokeSync(paySuccessMessage);
//
//        } catch (RemotingException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        OrderDO orderInfoByOrderNumber = orderWechatService.getOrderInfoByOrderNumber(orderNumber);

        System.out.println("收到需要发货事务消息:"+"订单号是 "+ orderNumber
        );
        boolean b = shipDaoImp.shipProductToPlayerPostBox(orderInfoByOrderNumber);
        if (b){
            System.out.println("报告已经写入mongodb数据库中");
        }
        GeneralMessage generalMessage = new GeneralMessage();
        generalMessage.setContent("恭喜你成功付款,但是哥们不打算发货 芜湖~~"+orderNumber);
        Gson gson= new Gson();
        String json = gson.toJson(generalMessage);
        BotAPI.getInstance().nettyWebSocketServerHandler.sendOneWay(json,"*bukkit");
        BotAPI.getInstance().sendGroupMsg(203728839L, orderInfoByOrderNumber.getBuyerName()+"你购买的商品,订单:"+orderInfoByOrderNumber.getOrderNumber()+"已经发货");
    }
}
