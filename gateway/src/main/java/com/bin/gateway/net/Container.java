package com.bin.gateway.net;

import com.bin.gateway.cache.ServerCache;
import com.bin.gateway.discovery.ServiceDiscovery;
import com.bin.gateway.discovery.imple.NacosDiscovery;
import com.bin.gateway.net.server.NettyServer;
import com.bin.gateway.process.AbstractProcess;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: bin
 * @date: 2023/12/15 17:34
 **/
public class Container implements InitializingBean, DisposableBean {

    private int coolServerPort;

    private int coreThreadNumber;

    private int maxThreadNumber;

    private String adders;

    private String group;

    private NettyServer nettyServer;

    private ServiceDiscovery serviceDiscovery;
    @Autowired
    private AbstractProcess process;

    @Autowired
    private ServerCache serverCache;


    @Override
    public void afterPropertiesSet() {
        serviceDiscovery = new NacosDiscovery(adders,group,serverCache);
        nettyServer = new NettyServer(coolServerPort,coreThreadNumber,maxThreadNumber,process);
        //启动
        nettyServer.start();
        serviceDiscovery.start();
    }

    @Override
    public void destroy() throws Exception {
        if(nettyServer != null){
            nettyServer.stop();
        }
        if(serviceDiscovery != null) {
            serviceDiscovery.stop();
        }
    }

    public int getCoolServerPort() {
        return coolServerPort;
    }

    public void setCoolServerPort(int coolServerPort) {
        this.coolServerPort = coolServerPort;
    }

    public int getCoreThreadNumber() {
        return coreThreadNumber;
    }

    public void setCoreThreadNumber(int coreThreadNumber) {
        this.coreThreadNumber = coreThreadNumber;
    }

    public int getMaxThreadNumber() {
        return maxThreadNumber;
    }

    public void setMaxThreadNumber(int maxThreadNumber) {
        this.maxThreadNumber = maxThreadNumber;
    }

    public String getAdders() {
        return adders;
    }

    public void setAdders(String adders) {
        this.adders = adders;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
