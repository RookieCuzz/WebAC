package com.cuzz.webac.controller;

import com.cuzz.webac.caches.Caches;
import com.cuzz.webac.model.dto.SmsSendRequestDTO;
import com.cuzz.webac.model.dto.SmsSendResultDTO;
import com.cuzz.webac.service.SmsService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
public class SmsController {
    private final SmsService smsService;
    public SmsController(SmsService smsService) { this.smsService = smsService; }
    @Resource
    private Caches caches;

    @CrossOrigin
    @PostMapping("/send-code")
    public ResponseEntity<SmsSendResultDTO> sendCode(@RequestBody SmsSendRequestDTO req) {

        //什么行为会发送短信验证码？
        // 登录 注册  重置密码     ->redis


        //什么情况下会消耗redis中的短信验证码

        // 时间过期,用户成功登录,用户成功注册  redis->delete

        //当像用户发送验证码的时候 检查redis中是否有存在未消耗的验证码,
        //若有则复用旧的验证码 并续期
        SmsSendResultDTO res = smsService.sendCode(req.getMobile(), 30);
        if (res.isSuccess()){
            caches.setKey("SmsCache:"+req.getMobile(),res.getCode(),30, TimeUnit.MINUTES);
        }
        return ResponseEntity.status(res.getStatusCode() == 0 ? 500 : res.getStatusCode()).body(res);
    }
}