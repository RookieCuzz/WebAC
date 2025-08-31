package com.cuzz.webac.controller.shop;

import com.alibaba.fastjson.JSON;
import com.alipay.remoting.exception.RemotingException;
import com.cuzz.webac.broker.SpringClient;
import com.cuzz.webac.caches.Caches;
import com.cuzz.webac.model.doo.OrderDO;
import com.cuzz.webac.model.dto.RequestOrderInfoDTO;
import com.cuzz.webac.model.dto.WeChatPayNotificationDTO;
import com.cuzz.webac.model.dto.WeChatPaymentResponseDTO;
import com.cuzz.webac.model.vo.QRCodeVO;
import com.cuzz.webac.service.rocketmq.producer.RocketMQProducerService;
import com.cuzz.webac.service.OrderWechatService;
import com.cuzz.webac.utils.OrderUtils;
import com.cuzz.webac.utils.PaymentStatus;
import com.cuzz.webac.utils.Result;
import com.cuzz.webac.utils.constants.RedisConstants;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;



@Tag(name = "订单管理")
@RestController
@RequestMapping("/order")
public class OrderController {




    @Resource
    private OrderWechatService orderService;


    @Resource
    private Caches orderCaches;
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
    @Operation(summary = "取消订单")
    @PostMapping("/cancel/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId) {

        return Result.ok("取消成功");
    }
    @Operation(summary = "创建订单")
//    @ApiResponse(reference = "付款二维码")
    @PostMapping("/createOrder")
    public Result createOrder(@RequestBody RequestOrderInfoDTO info) throws WriterException {

        QRCodeVO qrCode = orderService.saveOrderAndReturnQRCode(info);

        if (qrCode==null){
            return Result.error(444,"获取付款链接失败");
        }
//        放入缓存,考虑后续用redis
//        this.orderCaches.getCacheOrders().put(qrCode.getOrderId(), qrCode);
//        String orderKey = RedisConstants.getOrderKey(qrCode.getOrderId());
//        this.orderCaches.putCache(orderKey,qrCode);
        rocketMQProducerService.sendDelayMessage("OrderTimeOut",qrCode.getOrderId(),1800);


        return Result.ok(qrCode);
    }


    @Operation(summary = "获取某页订单")
//    @CrossOrigin(origins = "http://127.1.0.1:8866")  // 允许来自指定域名的跨域请求
    @GetMapping("/getPageOrder")
    @CrossOrigin
    public Result getPageOrder(@RequestParam(defaultValue="1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize
                               ){
        PageInfo pageOrder = orderService.getPageOrder(pageNum, pageSize);
        Result<PageInfo> ok = Result.ok(pageOrder);
        return ok;
    }
//    @GetMapping("/getOrderInfoById/{orderId}")
//    public Result getOrderInfoByOrderId(@PathVariable("orderId") int orderId){
//
//        OrderDO orderInfoByOrderId = orderService.getOrderInfoByOrderId(orderId);
//        Result<OrderDO> ok = Result.ok(orderInfoByOrderId);
//        return ok;
//
//    }


    @Operation(summary = "通过订单号返回订单信息")
    @GetMapping("/getOrderInfoByNumber/{orderNumber}")
    public Result getOrderInfoByOrderId(@PathVariable("orderNumber") String orderNumber){

        OrderDO orderInfoByOrderId = orderService.getOrderInfoByOrderNumber(orderNumber);
        Result<OrderDO> ok = Result.ok(orderInfoByOrderId);
        return ok;

    }

    @Operation(summary = "查询某订单是否成功支付")
    @GetMapping("/getOrderIsPaySuccess")
    public Result getOrderIsPaySuccess(String orderNumber){

        byte b = orderService.queryOrderPayMentStatus(orderNumber);
        if (b==-1){
            return Result.error(444,"订单不存在");
        }else {
            return Result.ok(PaymentStatus.fromCode(b));
        }

    }

    @Operation(summary = "查询某订单是否发货")
    @GetMapping("/getOrderIsShipped")
    public Result getOrderIsShipped(String orderNumber){

        byte b = orderService.queryOrderStatus(orderNumber);
        if (b==-1){
            return Result.error(444,"订单不存在");
        }else {
            return Result.ok(PaymentStatus.fromCode(b));
        }

    }


    @Operation(summary = "微信支付成功回调接口")
    @PostMapping("/wechatOrderCallback")
    public Result wechatOrderCallback(@RequestBody WeChatPayNotificationDTO info) throws WriterException, RemotingException, InterruptedException {
//        RookiePaySuccessMessage paySuccessMessage = new RookiePaySuccessMessage();
        System.out.println(info);
        WeChatPayNotificationDTO.Resource resource = info.getResource();
        try {
            String decrypt = OrderUtils.decryptToString( resource.getAssociatedData().getBytes(),resource.getNonce().getBytes(), resource.getCiphertext(), orderService.getApiV3Key().getBytes());
            // 将字节数组转换为字符串，假设使用 UTF-8 编码
            System.out.println("解密后的结果：" + decrypt);
            WeChatPaymentResponseDTO weChatPaymentResponseDTO = JSON.parseObject(decrypt, WeChatPaymentResponseDTO.class);
            String outTradeNo = weChatPaymentResponseDTO.getOutTradeNo();
            OrderDO orderDO = (OrderDO) this.orderCaches.getCache(RedisConstants.getOrderKey(outTradeNo));
            if (orderDO==null){
                throw new Exception("订单在缓存中不存在,设计有缺陷需要到数据库查询");
            }
//            paySuccessMessage.setPlayer(qrCodeVO.getBuyer());
//            paySuccessMessage.setOrderId(outTradeNo);

//            //通知游戏服务器集群 发货
//            springClient.getBrokerClient().invokeSync(paySuccessMessage);

            //通知发货  更新赞助榜   QQBot发送消息给管理员群里广播
            rocketMQProducerService.sendTransactionMessage(outTradeNo,"OrderNeedShipMessage");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //通知服务器
        return Result.ok("ok");
    }


}
