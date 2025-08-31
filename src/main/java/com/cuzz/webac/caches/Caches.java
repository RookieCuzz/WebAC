package com.cuzz.webac.caches;

import com.cuzz.webac.model.vo.QRCodeVO;
import com.cuzz.webac.service.RedisService;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Data
public class Caches {

    private final Map<String, QRCodeVO> cacheOrders= new ConcurrentHashMap<>();


    @Resource
    RedisService redisService;



    //将key直接放入redis
    public void putCache(String oldKey,Object value){

        redisService.setCacheObject(oldKey,value);

    };


    public void putCache(String key,Object object, long timeout, TimeUnit timeUnit){
        redisService.setCacheObject(key,object,timeout,timeUnit);
    }
    public void setKey(String key,String object, long timeout, TimeUnit timeUnit){
        redisService.setKey(key,object,timeout,timeUnit);
    }
    public String getKey(String key){
        return redisService.getKey(key);
    }
    public Object getCache(String key){
        Object cacheObject = redisService.getCacheObject(key);
        return cacheObject;
    }


    public void removeCache(String key){
        redisService.deleteObject(key);
    }

}
