package com.cuzz.webac.broker;

import com.alipay.remoting.exception.RemotingException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClient;
import net.afyer.afybroker.client.BrokerClientBuilder;
import net.afyer.afybroker.core.BrokerClientType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;


//@Slf4j
@Service
public class SpringClient {

    @Value("${afybroker.host}")
    private String gatewayHost;

    @Value("${afybroker.port}")
    private Integer gatewayPort;

    @Value("${afybroker.name}")
    private String name;

    private BrokerClient brokerClient;
    public BrokerClient getBrokerClient() {
        System.out.println(gatewayHost);
        if (brokerClient == null) {
            brokerClient = BrokerClient.newBuilder()
                    .host(gatewayHost)
                    .port(gatewayPort)
                    .name(name)
                    .addTag("spring")
                    .type("Spring")
                    .build();
        }

        System.out.println(gatewayPort+gatewayHost);
        return brokerClient;
    }

    @PostConstruct
    public void init() throws RemotingException, InterruptedException {
        System.out.println("PostConstruct: Broker has been initialized...");
        getBrokerClient();
        brokerClient.startup();
        brokerClient.ping();

    }
}
