package com.bin.access.service;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.bin.api.router.RouterServiceApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * @author: bin.jiang
 * @date: 2024/6/25 17:13
 **/
@Component
public class NettyServerRegister {
    private NamingService namingService;
    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String serverAddr = "";
    @Value("${spring.application.name}")
    String serviceName;
    @Value("${im.server.port}")
    Integer port;
    private String localIp = "";

    @DubboReference(check = false)
    private RouterServiceApi routerServiceApi;

    @PostConstruct
    public void init() throws NacosException, UnknownHostException {
        // 注册服务
        InetAddress localHost = InetAddress.getLocalHost();
        //获取本机ip地址
        localIp = localHost.getHostAddress();

        // 创建 Nacos 客户端实例
        Properties properties = new Properties();
        properties.setProperty("serverAddr", serverAddr);
        namingService = NacosFactory.createNamingService(properties);
    }

    public void registerService() {
        try {
            namingService.registerInstance(serviceName, localIp, port);
            System.out.println("Service registered: " + serviceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deregisterService() {
        try {
            // 注销服务
            namingService.deregisterInstance(serviceName, localIp, port);
            System.out.println("Service deregistered: " + serviceName);
            // 服务下线，通知路由服务
            routerServiceApi.ServerOff(localIp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 其他方法，例如服务订阅、健康检查等
    public static void main(String[] args) throws Exception {
        String serverAddr = "127.0.0.1:8848"; // Nacos 服务器地址
        int port = 8099; // WebSocket 服务端口
        String ip = "192.168.101.1"; // WebSocket 服务IP地址
        String serviceName = "access"; // 服务名

        Properties properties = new Properties();
        properties.setProperty("serverAddr", serverAddr);
        NamingService namingService = NacosFactory.createNamingService(properties);
        namingService.registerInstance(serviceName, ip, port);

        // 启动 WebSocket 服务的逻辑...

        // 服务关闭时注销
        // registry.deregisterService(serviceName, ip, port);
    }
}

