package com.cuzz.webac.controller;

import com.cuzz.webac.caches.Caches;
import com.cuzz.webac.service.MailService;
import com.cuzz.webac.utils.Result;
import com.cuzz.webac.utils.SpawnCheckNumber;
import com.cuzz.webac.utils.constants.RedisConstants;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class MailController {

    @Autowired
    MailService mailService;

    @Resource
    Caches orderCaches;




    @GetMapping("/mail/send")
    @CrossOrigin
    private Result send(String email) throws Exception{
        System.out.println("来了老弟");
//        email="1373716338@qq.com";
        SpawnCheckNumber checkNumber = mailService.sendCheckNumber(email);
        if (checkNumber==null){
            return Result.error(444,"获取验证码失败");
        }
        // 将验证码存入 Redis，设置过期时间
        String mailKey = RedisConstants.getMailKey(email);
        orderCaches.putCache(mailKey, checkNumber.getNumber(),RedisConstants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
//        return checkNumber;
        return Result.ok("验证码已发送，请前往邮箱查看!");
    }

}
