package com.cuzz.webac.controller;


import com.cuzz.webac.model.doo.AccountDO;
import com.cuzz.webac.model.dto.ACDTO;
import com.cuzz.webac.servers.AccountService;
import com.cuzz.webac.servers.JwtService;
import com.cuzz.webac.utils.Result;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public Result accountRegister(@RequestBody ACDTO acdto) throws Exception {

        System.out.println(acdto.getPassword());
        if(!accountService.isValidRegisterInfo(acdto))
            return Result.error(400,"你输入的信息不符合规定");

        if (!accountService.isEmailMatchCode(acdto))
            return Result.error(400,"你输入的验证码不正确");


        accountService.registerAccount(acdto);
        logger.info("注册成功!");

        return Result.ok(acdto);
    }



    @PostMapping("/login")
    public Result accountLogin(@RequestBody ACDTO acdto){
        AccountDO accountByEmail = accountService.getAccountByEmail(acdto.getEmail());

        if (!accountService.isAccountExist(accountByEmail))
            return Result.error(400,"邮箱不存在");


        if (accountService.isPasswordCorrect(accountByEmail, acdto.getPassword())){
            String token = jwtService.generateToken(acdto.getUsername());
            return Result.ok(token);
        }


        return Result.error(400,"密码错误");
    }
}
