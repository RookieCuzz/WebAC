package com.cuzz.webac.controller.shop;

import com.alibaba.fastjson.JSON;
import com.alipay.remoting.exception.RemotingException;
import com.cuzz.common.rookiepay.RookiePaySuccessMessage;
import com.cuzz.webac.broker.SpringClient;
import com.cuzz.webac.caches.OrderCaches;
import com.cuzz.webac.model.doo.OrderDO;
import com.cuzz.webac.model.dto.RequestOrderInfoDTO;
import com.cuzz.webac.model.dto.WeChatPayNotificationDTO;
import com.cuzz.webac.model.dto.WeChatPaymentResponseDTO;
import com.cuzz.webac.model.vo.QRCodeVO;
import com.cuzz.webac.servers.rocketmq.producer.RocketMQProducerService;
import com.cuzz.webac.servers.WechatService;
import com.cuzz.webac.utils.OrderUtils;
import com.cuzz.webac.utils.Result;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import jakarta.annotation.Resource;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {




    @Resource
    private WechatService orderService;


    @Resource
    private OrderCaches orderCaches;
    @Resource
    private RocketMQProducerService rocketMQProducerService;
    @Resource
    private SpringClient springClient;
    //    @PostMapping("/pay/{orderId}")
//    public Result tryPayOrder(@PathVariable("orderId") Long orderId,
//                              @RequestBody PaymentRequest paymentRequest) {
//        // 在这里处理支付逻辑，调用支付服务
//        // 比如校验订单信息、处理支付等
//
//        // 返回支付结果
//        return Result.ok("支付请求成功处理");
//    }
    @PostMapping("/cancel/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId) {

        return Result.ok("取消成功");
    }

    @PostMapping("/createOrder")
    public Result createOrder(@RequestBody RequestOrderInfoDTO info) throws WriterException {
        QRCodeVO qrCode = orderService.getQRCode(info);

        //放入缓存,考虑后续用redis
        this.orderCaches.getCacheOrders().put(qrCode.getOrderId(), qrCode);

        rocketMQProducerService.sendDelayMessage("OrderTimeOut","oi,请检查 "+qrCode.getOrderId() + "是否超时",120);


        return Result.ok(qrCode);
    }


    @CrossOrigin(origins = "http://127.1.0.1:8866")  // 允许来自指定域名的跨域请求
    @GetMapping("/getPageOrder")
    public Result getPageOrder(@RequestParam(defaultValue="1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize
                               ){
        PageInfo pageOrder = orderService.getPageOrder(pageNum, pageSize);
        Result<PageInfo> ok = Result.ok(pageOrder);
        return ok;
    }
    @GetMapping("/getOrderInfoById/{orderId}")
    public Result getOrderInfoByOrderId(@PathVariable("orderId") int orderId){

        OrderDO orderInfoByOrderId = orderService.getOrderInfoByOrderId(orderId);
        Result<OrderDO> ok = Result.ok(orderInfoByOrderId);
        return ok;

    }

    @GetMapping("/getOrderInfoByNumber/{orderNumber}")
    public Result getOrderInfoByOrderId(@PathVariable("orderNumber") String orderNumber){

        OrderDO orderInfoByOrderId = orderService.getOrderInfoByOrderNumber(orderNumber);
        Result<OrderDO> ok = Result.ok(orderInfoByOrderId);
        return ok;

    }
    @PostMapping("/wechatOrderCallback")
    public Result wechatOrderCallback(@RequestBody WeChatPayNotificationDTO info) throws WriterException, RemotingException, InterruptedException {
        RookiePaySuccessMessage paySuccessMessage = new RookiePaySuccessMessage();

        WeChatPayNotificationDTO.Resource resource = info.getResource();
        try {
            String decrypt = OrderUtils.decryptToString( resource.getAssociatedData().getBytes(),resource.getNonce().getBytes(), resource.getCiphertext(), orderService.getApiV3Key().getBytes());
            // 将字节数组转换为字符串，假设使用 UTF-8 编码
            System.out.println("解密后的结果：" + decrypt);
            WeChatPaymentResponseDTO weChatPaymentResponseDTO = JSON.parseObject(decrypt, WeChatPaymentResponseDTO.class);
            String outTradeNo = weChatPaymentResponseDTO.getOutTradeNo();
            QRCodeVO qrCodeVO =  this.orderCaches.getCacheOrders().get(outTradeNo);
            if (qrCodeVO==null){
                throw new Exception("订单在缓存中不存在,设计有缺陷需要到数据库查询");
            }
            paySuccessMessage.setPlayer(qrCodeVO.getBuyer());
            paySuccessMessage.setOrderId(outTradeNo);

//            //通知游戏服务器集群 发货
//            springClient.getBrokerClient().invokeSync(paySuccessMessage);

            //通知发货  更新赞助榜   QQBot发送消息给管理员群里广播
            rocketMQProducerService.sendTransactionMessage(outTradeNo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //通知服务器
        return Result.ok("ok");
    }


}
