package com.cuzz.webac.utils.constants;


import java.util.concurrent.TimeUnit;

public class RedisConstants {

    // 存储邮箱验证码的 Redis 键前缀
    public static final String EMAIL_CODE = "EMAIL_CODE:";

    // 验证码的默认过期时间，单位是分钟
    public static final long CAPTCHA_EXPIRATION = 5; // 5 分钟

    // 其他 Redis 常量...
}
