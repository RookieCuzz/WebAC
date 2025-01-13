package com.cuzz.webac.broker.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.cuzz.common.rookiepay.RookiePayTryOrderMessage;
import com.cuzz.webac.servers.WechatService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


@Component
public class CreateOrderProcessor extends SyncUserProcessor<RookiePayTryOrderMessage> {


//    @Resource
//    WechatService orderService
//    @Override
//    public Object handleRequest(BizContext bizContext, RookiePayTryOrderMessage rookiePayTryOrderMessage) throws Exception {
//
//
//        //放入缓存,考虑后续用redis
//        this.orderCaches.getCacheOrders().put(qrCode.getOrderId(), qrCode);
//
//        rocketMQProducerService.sendDelayMessage("OrderTimeOut","oi,请检查 "+qrCode.getOrderId() + "是否超时",120);
//
//
//
//
//        return null;
//    }

    @Override
    public Object handleRequest(BizContext bizContext, RookiePayTryOrderMessage rookiePayTryOrderMessage) throws Exception {
        return null;
    }

    @Override
    public String interest() {
        return RookiePayTryOrderMessage.class.getName();
    }
}
