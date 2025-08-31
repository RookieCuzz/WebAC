package com.cuzz.webac.utils.constants;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ShopConfig {

    @Value("${shop.orderTimeOut}")
    Integer orderTimeOut;

    @Value("${shop.wechatPaySuccessCallBack}")
    String wechatPaySuccessCallBack;
}
