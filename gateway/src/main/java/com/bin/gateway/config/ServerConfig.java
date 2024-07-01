package com.bin.gateway.config;

import com.bin.gateway.net.Container;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: bin
 * @date: 2023/12/13 17:02
 **/
@Configuration
@Slf4j
public class ServerConfig {
    @Value("${cool.registerCenter.adders}")
    private String adders;

    @Value("${cool.registerCenter.group}")
    private String group;

    @Value("${cool.gateway.port:9000}")
    private int coolServerPort;

    @Value("${cool.server.coreThreadNumber:30}")
    private int coreThreadNumber;

    @Value("${cool.server.maxThreadNumber:50}")
    private int maxThreadNumber;

    @Bean
    public Container container(){
        Container container = new Container();
        container.setAdders(adders);
        container.setGroup(group);
        container.setCoolServerPort(coolServerPort);
        container.setCoreThreadNumber(coreThreadNumber);
        container.setMaxThreadNumber(maxThreadNumber);
        return container;
    }

}
