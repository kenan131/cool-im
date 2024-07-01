package com.bin.gateway.discovery.imple;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.bin.gateway.cache.ServerCache;
import com.bin.gateway.common.GatewayConst;
import com.bin.gateway.discovery.ServiceDiscovery;
import com.bin.gateway.model.CoolServerInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: bin
 * @date: 2023/12/14 15:44
 **/
@Slf4j
public class NacosDiscovery implements ServiceDiscovery {

    private ServerCache serverCache;

    //注册中心地址
    private String registerCenterAddress;

    private String group;

    /**
     * 主要用于维护服务实例信息
     */
    private NamingService namingService;

    private ScheduledExecutorService scheduledThreadPool;

    /**
     * 主要用于维护服务定义信息
     */
    private NamingMaintainService namingMaintainService;


    public NacosDiscovery(String address, String group,ServerCache serverCache){
        this.registerCenterAddress = address;
        this.group = group;
        this.serverCache = serverCache;
        try {
            namingService = NamingFactory.createNamingService(registerCenterAddress);
        } catch (NacosException e) {
            log.error("NamingFactory.createNamingService error");
            throw new RuntimeException(e);
        }
    }
    public void start(){
        //定时去获取最新服务实例
        scheduledThreadPool = Executors.newScheduledThreadPool(1, new NameThreadFactory("doSubscribeAllServices"));
        scheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                discovery();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        scheduledThreadPool.shutdown();
    }

    public void discovery(){
        int pageNo = 1;//第一页
        int pageSize = 100;//页大小
        Set<String> subscribeServiceSet;
        List<String> serviseList;
        try{
            subscribeServiceSet = namingService.getSubscribeServices()
                    .stream().map(ServiceInfo::getName).collect(Collectors.toSet());
            serviseList = namingService.getServicesOfServer(pageNo, pageSize).getData();
            while(CollectionUtils.isNotEmpty(serviseList)){
                for (String service : serviseList) {
                    //当已经订阅了该服务，则表明已注册过监听器。
                    if (subscribeServiceSet.contains(service)) {
                        continue;
                    }
                    //nacos监听器
                    EventListener eventListener = new NacosEventListener();
                    //注册监听器
                    namingService.subscribe(service, group, eventListener);
                    log.info("subscribe a service ，ServiceName {} group {}", service, group);
                }
                serviseList = namingService.getServicesOfServer(++pageNo, pageSize).getData();
            }
        }catch (NacosException e){
            log.error("nacos discovery thread trigger error："+e);
        }
    }


    public class NacosEventListener implements EventListener {
        @Override
        public void onEvent(Event event) {
            //先判断是否是注册中心事件
            if (event instanceof NamingEvent) {
                NamingEvent namingEvent = (NamingEvent) event;
                //获取当前变更的服务名
                String serviceName = namingEvent.getServiceName();
                log.info("the serviceName is update,event：{}", JSON.toJSON(event));
                List<Instance> instances = ((NamingEvent) event).getInstances();
                List<CoolServerInstance> coolServers = null;
                //nacos获取的服务名，默认是  分组名称@@服务名  "DEFAULT_GROUP@@test-sample";
                String[] strs = serviceName.split(GatewayConst.ServiceNameSplit);
                if (instances != null){
                    coolServers = instances.stream().map(a -> JSON.parseObject(JSON.toJSONString(a), CoolServerInstance.class))
                            .collect(Collectors.toList());
                    serverCache.setInstances(strs[1],coolServers);
                }
                else{
                    serverCache.removeInstances(strs[1]);
                }
            }
        }
    }

}
