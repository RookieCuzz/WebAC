package com.cuzz.webac.broker.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.cuzz.common.rookiepay.RookiePayTryOrderMessage;
import com.cuzz.common.rookiepay.RookiePayTryOrderResponse;
import com.cuzz.webac.model.vo.OrderInfoVO;
import com.cuzz.webac.servers.WechatService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


@Component
public class TryOrderProcessor extends SyncUserProcessor<RookiePayTryOrderMessage> {



    @Resource
    WechatService orderService;
    @Override
    public Object handleRequest(BizContext bizContext, RookiePayTryOrderMessage rookiePayTryOrderMessage) throws Exception {

        RookiePayTryOrderResponse oderInfoVo = orderService.createOderInfoVo(rookiePayTryOrderMessage);
        return oderInfoVo;
    }

    @Override
    public String interest() {
        return RookiePayTryOrderMessage.class.getName();
    }
}
