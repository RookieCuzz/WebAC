package com.cuzz.webac.service;


import com.cuzz.webac.model.doo.OrderDO;
import com.cuzz.webac.utils.constants.RedisConstants;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Resource
    private RedisTemplate<String, String> redisTemplate; // 修改为 String, String，避免序列化问题

    @PostConstruct
    public void verify() {
        // 验证 Redis 连接是否正常
        try {
            redisTemplate.getConnectionFactory().getConnection();
            System.out.println("Redis connection is verified successfully.");
        } catch (Exception e) {
            System.err.println("Failed to verify Redis connection: " + e.getMessage());
        }
    }

    /**
     * 生成新的 Redis Key
     * @param oldKey 原始 Key
     * @param object 对象
     * @return 新的 Key
     */
    public String generateNewKey(String oldKey, Object object) {
        if (object instanceof OrderDO) {
            return RedisConstants.ORDER_PREFIX + oldKey;
        }
        // 可以根据需要扩展更多类型
        return oldKey;
    }

    /**
     * 序列化对象为字符串
     * @param object 要序列化的对象
     * @return 序列化后的字符串
     * @throws IOException 如果序列化失败
     */
    public String serialize(Object object) throws IOException {
        if (object == null) {
            throw new IllegalArgumentException("Object to serialize cannot be null");
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            oos.flush();
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        }
    }

    /**
     * 从字符串反序列化对象
     * @param base64Str 序列化后的字符串
     * @return 反序列化后的对象
     * @throws IOException 如果反序列化失败
     * @throws ClassNotFoundException 如果反序列化的类未找到
     */
    public Object deserialize(String base64Str) throws IOException, ClassNotFoundException {
        if (base64Str == null || base64Str.isEmpty()) {
            throw new IllegalArgumentException("Base64 string to deserialize cannot be null or empty");
        }
        byte[] bytes = Base64.getDecoder().decode(base64Str);

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }

    /**
     * 获取 key 对应的值
     * @param key 键
     * @return 返回存储的值
     */
    public Object getCacheObject(String key) {
        String serializedValue = redisTemplate.opsForValue().get(key);
        if (serializedValue == null) {
            return null;
        }
        try {
            return deserialize(serializedValue);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize object from Redis", e);
        }
    }

    /**
     * 存储 key-value 数据到 Redis
     * @param key 键
     * @param value 值
     */
    public void setCacheObject(String key, Object value) {
        try {
            String serializedValue = serialize(value);
            redisTemplate.opsForValue().set(key, serializedValue);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize object for Redis", e);
        }
    }

    /**
     * 存储 key-value 数据到 Redis，并设置过期时间
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     */
    public void setCacheObject(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            String serializedValue = serialize(value);
            redisTemplate.opsForValue().set(key, serializedValue, timeout, timeUnit);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize object for Redis", e);
        }
    }

    /**
     * 删除 key
     * @param key 键
     */
    public void deleteObject(String key) {
        redisTemplate.delete(key);
    }


    public void setKey(String key, String value, long timeout, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);

    }
    public String getKey(String key){
        return redisTemplate.opsForValue().get(key);

    }
}
