package com.cuzz.webac;

import com.cuzz.webac.broker.SpringClient;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.core.BrokerClientInfo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
@OpenAPIDefinition
@MapperScan("com.cuzz.webac.mapper")  // 确保扫描到 Mapper 包
public class WebAcApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(WebAcApplication.class, args);

        // 获取指定 Bean
        SpringClient springClient = context.getBean(SpringClient.class);

    }

}
