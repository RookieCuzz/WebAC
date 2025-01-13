package com.cuzz.webac.controller;

import com.cuzz.webac.servers.MailService;
import com.cuzz.webac.utils.Result;
import com.cuzz.webac.utils.SpawnCheckNumber;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {

    @Autowired
    MailService mailService;

    @GetMapping("/mail/send")
    private Result send(String email) throws Exception{
        System.out.println("来了老弟");
//        email="1373716338@qq.com";
        SpawnCheckNumber checkNumber = mailService.sendCheckNumber(email);
//        return checkNumber;
        return Result.ok("验证码已发送，请前往邮箱查看!");
    }

}
