package com.cuzz.webac.servers;
import com.cuzz.common.rookiepay.RookiePayTryOrderMessage;
import com.cuzz.common.rookiepay.RookiePayTryOrderResponse;
import com.cuzz.webac.dao.OrderDao;
import com.cuzz.webac.model.doo.OrderDO;
import com.cuzz.webac.model.dto.RequestOrderInfoDTO;
import com.cuzz.webac.model.vo.OrderInfoVO;
import com.cuzz.webac.model.vo.QRCodeVO;
import com.cuzz.webac.servers.rocketmq.producer.RocketMQProducerService;
import com.cuzz.webac.utils.OrderStatus;
import com.cuzz.webac.utils.OrderUtils;
import com.cuzz.webac.utils.PaymentStatus;
import com.cuzz.webac.utils.QRCodeGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WechatService {

    @Autowired
    private OrderDao orderDao;

    @Resource
    private RocketMQProducerService rocketMQProducerService;

    /** 商户号 */
    @Value("${wechatpay.merchantId}")
    private String merchantId;
    /** 商户API私钥路径 */
    @Value("${wechatpay.privateKeyPath}")
    private String privateKeyPath;
    /** 商户证书序列号 */
    @Value("${wechatpay.merchantSerialNumber}")
    private String merchantSerialNumber;
    /** 商户APIV3密钥 */
    @Getter
    @Value("${wechatpay.apiV3Key}")
    private String apiV3Key;

    //需使用应用属性为公众号的APPID
    @Value("wx8d9bdda51f58af31")
    private String appid;

    NativePayService service;
    private static Config config;

    public WechatService(){

    }


    // 配置初始化方法
    @PostConstruct
    public void setUpConfig() {
        try {
            // 使用 ClassPathResource 获取资源文件
            ClassPathResource resource = new ClassPathResource("apiclient_key.pem");

            // 将 InputStream 写入一个临时文件
            File tempFile = File.createTempFile("apiclient_key", ".pem");
            try (InputStream inputStream = resource.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // 使用自动更新平台证书的RSA配置，通过文件路径读取私钥
            this.config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(merchantId)
                    .privateKeyFromPath(tempFile.getAbsolutePath())  // 使用临时文件的路径
                    .merchantSerialNumber(merchantSerialNumber)
                    .apiV3Key(apiV3Key)
                    .build();

            // 删除临时文件（可选）
            tempFile.deleteOnExit();
            service = new NativePayService.Builder().config(config).build();
        } catch (IOException e) {
            throw new RuntimeException("加载私钥文件失败", e);
        }
    }


    public Boolean updateOrderInfo(OrderDO orderDO){

        Boolean b = orderDao.updateOrderInfo(orderDO);

        return b;
    }


    public Boolean createNewOrder(RequestOrderInfoDTO infoDTO, String orderNumber){
        System.out.println(infoDTO.toString());
        OrderDO orderDO = OrderDO.builder().orderNumber(orderNumber)
                .actualPayment(BigDecimal.valueOf(infoDTO.getMoney()))
                .buyerName(infoDTO.getBuyerName())
                .consigneeName(infoDTO.getUserName())
                .consigneeUuid(infoDTO.getUserUUID())
                .productQuantity(infoDTO.getProductAmount())
                .orderStatus(OrderStatus.PENDING_PAYMENT.getCode())
                .paymentStatus(PaymentStatus.UNPAID.getCode())
                .orderDescription(infoDTO.getDescription())
                .totalDiscount(BigDecimal.valueOf(0))
                .paymentMethod(infoDTO.getPayment())
                .address(infoDTO.getAddress()).build();
        orderDao.spawnNewOrder(orderDO);

        return true;
    }

    public Boolean createNewOrder(RookiePayTryOrderMessage rookiePayTryOrderMessage, String orderNumber){

        OrderDO orderDO = OrderDO.builder().orderNumber(orderNumber)
                .actualPayment(BigDecimal.valueOf(rookiePayTryOrderMessage.getMoney()))
                .buyerName(rookiePayTryOrderMessage.getBuyerName())
                .consigneeName(rookiePayTryOrderMessage.getUserName())
                .consigneeUuid(rookiePayTryOrderMessage.getUserUUID())
                .productQuantity(rookiePayTryOrderMessage.getProductAmount())
                .orderStatus((byte) 0)
                .paymentStatus((byte) 0)
                .orderDescription(rookiePayTryOrderMessage.getDescription())
                .totalDiscount(BigDecimal.valueOf(0))
                .paymentMethod(rookiePayTryOrderMessage.getPayment())
                .address("Game").build();
        orderDao.spawnNewOrder(orderDO);

        return true;
    }
    // rpc模式

    public RookiePayTryOrderResponse createOderInfoVo(RookiePayTryOrderMessage rookiePayTryOrderMessage){
        //创建订单 返回二维码
        Double amount = rookiePayTryOrderMessage.getMoney();
        PrepayRequest request = new PrepayRequest();
        request.setAppid(appid);
        request.setMchid(merchantId);
        Amount money = new Amount();
        money.setTotal((int)(amount*100)); // 设置支付金额 单位分
        request.setDescription(rookiePayTryOrderMessage.getDescription());
        request.setAmount(money);
        // Get the current time in Beijing time zone
        // 将当前时间加2小时，并去掉毫秒部分
        // 获取当前时间
        ZonedDateTime currentTime = ZonedDateTime.now();

        // Add 2 hours to the current time
        ZonedDateTime expirationTime = currentTime.plusHours(2).withNano(0);

        // Format the time to RFC3339
        String formattedExpirationTime = expirationTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        request.setTimeExpire(formattedExpirationTime);
        //设置微信官方通知我们的地址
        request.setNotifyUrl("https://www.4399mc.cn/cuzz/order/wechatOrderCallback");
        String orderID = OrderUtils.generateOrderNumber();
        request.setOutTradeNo(orderID);
        // 调用下单方法，得到应答
        PrepayResponse response = service.prepay(request);
        String codeUrl = response.getCodeUrl();

        //添加订单到数据库
        this.createNewOrder(rookiePayTryOrderMessage,orderID);
        String qrStr = null;
        try {
            qrStr = QRCodeGenerator.getQRStr(codeUrl);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        RookiePayTryOrderResponse orderInfo = new RookiePayTryOrderResponse();
        orderInfo.setOrderId(orderID);
        orderInfo.setQrcode(qrStr);
        orderInfo.setPrice(amount);
        orderInfo.setBuyer(rookiePayTryOrderMessage.getBuyerName());

        return orderInfo;

    }

    public PageInfo getPageOrder(int pageNum,int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<OrderDO> pageOrder = this.orderDao.getPageOrder();
        return new PageInfo<>(pageOrder);
    }

    public OrderInfoVO createOrderInfoVO(RequestOrderInfoDTO requestOrderInfoDTO) throws WriterException {
        Double amount = requestOrderInfoDTO.getMoney();
        PrepayRequest request = new PrepayRequest();
        request.setAppid(appid);
        request.setMchid(merchantId);
        Amount money = new Amount();
        money.setTotal((int)(amount*100)); // 设置支付金额 单位分
        request.setDescription(requestOrderInfoDTO.getDescription());
        request.setAmount(money);
        // Get the current time in Beijing time zone
        // 将当前时间加2小时，并去掉毫秒部分
        // 获取当前时间
        ZonedDateTime currentTime = ZonedDateTime.now();

        // Add 2 hours to the current time
        ZonedDateTime expirationTime = currentTime.plusHours(2).withNano(0);

        // Format the time to RFC3339
        String formattedExpirationTime = expirationTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        request.setTimeExpire(formattedExpirationTime);
        //设置微信官方通知我们的地址
        request.setNotifyUrl("https://www.4399mc.cn/cuzz/order/wechatOrderCallback");
        String orderID = OrderUtils.generateOrderNumber();
        request.setOutTradeNo(orderID);
        // 调用下单方法，得到应答
        PrepayResponse response = service.prepay(request);
        String codeUrl = response.getCodeUrl();
        String qrStr = QRCodeGenerator.getQRStr(codeUrl);
        OrderInfoVO orderInfo = new OrderInfoVO();
        orderInfo.setOrderId(orderID);
        orderInfo.setContent(qrStr);
        orderInfo.setBuyer(requestOrderInfoDTO.getBuyerName());

        return orderInfo;

    }


    public OrderDO getOrderInfoByOrderId(int orderId){
        OrderDO orderDoByOrderId = this.orderDao.getOrderDoByOrderId(orderId);
        return orderDoByOrderId;


    }

    public OrderDO getOrderInfoByOrderNumber(String orderNumber){
        OrderDO orderDO = this.orderDao.getOrderDoByOrderNumber(orderNumber);

        return orderDO;


    }
    public QRCodeVO getQRCode(RequestOrderInfoDTO infoDTO) throws WriterException {

        Double amount = infoDTO.getMoney();
        NativePayService service = new NativePayService.Builder().config(config).build();

        // 创建支付请求
        PrepayRequest request = new PrepayRequest();


        request.setAppid(appid);
        request.setMchid(merchantId);

        Amount money = new Amount();
        money.setTotal((int)(amount*100)); // 设置支付金额 单位分
        request.setDescription(infoDTO.getDescription());
        request.setAmount(money);
        // Get the current time in Beijing time zone
        // 将当前时间加2小时，并去掉毫秒部分
        // 获取当前时间
        ZonedDateTime currentTime = ZonedDateTime.now();

        // Add 2 hours to the current time
        ZonedDateTime expirationTime = currentTime.plusHours(2).withNano(0);

        // Format the time to RFC3339
        String formattedExpirationTime = expirationTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        request.setTimeExpire(formattedExpirationTime);
        //设置微信官方通知我们的地址
        request.setNotifyUrl("https://www.4399mc.cn/cuzz/order/wechatOrderCallback");
        String orderID = OrderUtils.generateOrderNumber();

        request.setOutTradeNo(orderID);
        // 调用下单方法，得到应答
        PrepayResponse response = service.prepay(request);
        String codeUrl = response.getCodeUrl();

        //添加订单到数据库
        this.createNewOrder(infoDTO,orderID);

        String qrStr = QRCodeGenerator.getQRStr(codeUrl);
        QRCodeVO qrCode = new QRCodeVO();
        qrCode.setOrderId(orderID);
        qrCode.setContent(qrStr);
        System.out.println(qrStr);
        qrCode.setBuyer(infoDTO.getBuyerName());
        return  qrCode;
    }
}
