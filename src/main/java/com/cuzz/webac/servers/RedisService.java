package com.cuzz.webac.servers;


import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 存储 key-value 数据到 Redis
     * @param key 键
     * @param value 值
     */
    public void setCacheObject(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 存储 key-value 数据并设置过期时间
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param timeUnit 时间单位（如秒、分钟）
     */
    public void setCacheObject(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 获取 key 对应的值
     * @param key 键
     * @return 返回存储的值
     */
    public Object getCacheObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置 key 的过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     * @return 如果成功，返回 true
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 删除 key
     * @param key 键
     */
    public void deleteObject(String key) {
        redisTemplate.delete(key);
    }
}
