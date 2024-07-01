package com.bin.gateway.filter.loadBalance;

import com.bin.gateway.cache.ServerCache;
import com.bin.gateway.common.CoolGateWayException;
import com.bin.gateway.common.GatewayConst;
import com.bin.gateway.common.ResponseCode;
import com.bin.gateway.model.CoolServerInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author: bin
 * @date: 2023/12/20 10:32
 **/

@Slf4j
public class RandomLoadBalance extends LoadBalance{


    public RandomLoadBalance(ServerCache cache) {
        super(cache);
    }

    @Override
    public CoolServerInstance getServerInstance(String serviceName,boolean isGray) {
        List<CoolServerInstance> coolServerInstances = serverCache.getInstanceListByServiceName(serviceName);
        if(coolServerInstances == null){
            log.error("according to {} not found serviceInstance",serviceName);
            throw new CoolGateWayException(ResponseCode.SERVICE_INSTANCE_NOT_FOUND);
        }
        if(isGray){
            List<CoolServerInstance> grayInstances = coolServerInstances.stream().filter((coolServerInstance) -> {
                Map<String, String> metadata = coolServerInstance.getMetadata();
                String gray = metadata.get(GatewayConst.GRAY);
                return "true".equals(gray);
            }).collect(Collectors.toList());
            if(!grayInstances.isEmpty()){
                coolServerInstances = grayInstances;
            }
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(coolServerInstances.size());
        return coolServerInstances.get(randomIndex);
    }
}
