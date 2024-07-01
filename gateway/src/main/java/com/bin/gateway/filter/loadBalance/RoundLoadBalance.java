package com.bin.gateway.filter.loadBalance;

import com.bin.gateway.cache.ServerCache;
import com.bin.gateway.common.CoolGateWayException;
import com.bin.gateway.common.GatewayConst;
import com.bin.gateway.common.ResponseCode;
import com.bin.gateway.model.CoolServerInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author: bin
 * @date: 2023/12/20 14:53
 **/
@Slf4j
public class RoundLoadBalance extends LoadBalance{

    //保存各实例待遍历的下标
    private ConcurrentHashMap<String,Integer> serviceMap = new ConcurrentHashMap();

    private Long timePoint = 0l;

    public RoundLoadBalance(ServerCache cache) {
        super(cache);
    }

    @Override
    CoolServerInstance getServerInstance(String serviceName,boolean isGray) {
        if(System.currentTimeMillis() > timePoint){
            serviceMap.clear();
            timePoint = System.currentTimeMillis() + (1000*60*60*24);//一天后清空。
        }
        List<CoolServerInstance> serverInstanceList = serverCache.getInstanceListByServiceName(serviceName);
        if(serverInstanceList == null) {
            log.error("according to {} not found serviceInstance",serviceName);
            throw new CoolGateWayException(ResponseCode.SERVICE_INSTANCE_NOT_FOUND);
        }
        Integer index = serviceMap.get(serviceName);
        if(index == null || index >= serverInstanceList.size()){
            index = 0;//如果为空，或者大于服务实例数则从头开始。
        }
        if(isGray){
            List<CoolServerInstance> grayInstances = serverInstanceList.stream().filter((coolServerInstance) -> {
                Map<String, String> metadata = coolServerInstance.getMetadata();
                String gray = metadata.get(GatewayConst.GRAY);
                return "true".equals(gray);
            }).collect(Collectors.toList());
            // 灰度流量，走随机
            if(grayInstances != null){
                int randomIndex = ThreadLocalRandom.current().nextInt(grayInstances.size());
                return grayInstances.get(randomIndex);
            }
            else{
                log.error("according to {} not found serviceInstance",serviceName);
                throw new CoolGateWayException(ResponseCode.SERVICE_INSTANCE_NOT_FOUND);
            }
        }
        CoolServerInstance coolServerInstance = serverInstanceList.get(index);
        index = (index+1)%serverInstanceList.size();
        serviceMap.put(serviceName,index);
        return coolServerInstance;
    }
}
