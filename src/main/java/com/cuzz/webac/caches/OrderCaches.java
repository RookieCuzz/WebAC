package com.cuzz.webac.caches;

import com.cuzz.webac.model.vo.QRCodeVO;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
public class OrderCaches {

    private final Map<String, QRCodeVO> cacheOrders= new ConcurrentHashMap<>();
}
