package com.cuzz.webac.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.cuzz.webac.model.doo.AccountDO;
import com.cuzz.webac.model.dto.LoginAccountDTO;
import com.cuzz.webac.model.dto.RegisterAccountDTO;
import com.cuzz.webac.service.AccountService;
import com.cuzz.webac.service.JwtService;
import com.cuzz.webac.utils.Result;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    // 使用 SLF4J Logger
    // 使用 SLF4J Logger
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);



    @Resource
    private JwtService jwtService;
    @Resource
    private AccountService accountService;
    @PostMapping("/register")
    @CrossOrigin
    public Result accountRegister(@RequestBody RegisterAccountDTO acdto) throws Exception {

        System.out.println(acdto.getPassword());
        if(!accountService.isValidRegisterInfo(acdto))
            return Result.error(400,"你输入的信息不符合规定");

        if (!accountService.isPhoneMatchCode(acdto.getUsername(),acdto.getVerificationCode()))
            return Result.error(400,"你输入的验证码不正确");


        AccountDO accountDO = accountService.registerAccount(acdto);
        StpUtil.login(accountDO.getNamex());
        logger.info("注册成功!");

        return Result.ok(acdto);
    }


    @PostMapping("/admin/generateApiKey")
    @CrossOrigin
    public Result generateApiKey(@RequestParam String ref){
        String apiKey = accountService.generateApiKey(ref);
        if (apiKey==null||apiKey.isEmpty()){
            return Result.error(444,"无法获取ApiKey");
        }

        return Result.ok(apiKey);

    }


    @PostMapping("/login")
    public Result accountLogin(@RequestBody LoginAccountDTO acdto){
        AccountDO accountByEmail = accountService.getAccountByEmail(acdto.getUsername());

        if (!accountService.isAccountExist(accountByEmail))
            return Result.error(400,"用户不存在");

        if (accountService.isPasswordCorrect(accountByEmail, acdto.getPassword())){
            String token = jwtService.generateToken(acdto.getUsername());


            StpUtil.login(accountByEmail.getId());
            return Result.ok(token);
        }


        return Result.error(400,"密码错误");
    }
}
