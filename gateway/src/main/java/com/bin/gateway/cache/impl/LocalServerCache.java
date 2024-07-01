package com.bin.gateway.cache.impl;

import com.bin.gateway.cache.ServerCache;
import com.bin.gateway.model.CoolServerInstance;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用local缓存服务实例
 * @author: bin
 * @date: 2023/12/15 11:37
 **/
@Component
public class LocalServerCache implements ServerCache {

    /*
        根据服务名，缓存服务实例
     */
    private Map<String, Set<CoolServerInstance>> instanceMap = new ConcurrentHashMap<>();

    //缓存list实例信息，方便顺序取值。
    private Map<String,List<CoolServerInstance>> allInstanceList = new ConcurrentHashMap<>();

    @Override
    public void setInstance(String serviceName, CoolServerInstance instance) {
        Set<CoolServerInstance> coolServerInstances = instanceMap.get(serviceName);
        if(coolServerInstances == null){
            coolServerInstances = new HashSet<>();
        }
        if(!coolServerInstances.contains(instance)){
            List<CoolServerInstance> serverInstanceList = allInstanceList.get(serviceName);
            if(serverInstanceList == null){
                serverInstanceList = new ArrayList<>();
                serverInstanceList.add(instance);
            }
            allInstanceList.put(serviceName,serverInstanceList);
        }
        instanceMap.put(serviceName,coolServerInstances);
    }

    @Override
    public void setInstances(String serviceName, List<CoolServerInstance> instances) {
        //每次设置实例前，都清空缓存。
        removeInstances(serviceName);
        Set<CoolServerInstance> coolServerInstances = new HashSet<>(instances);
        ArrayList<CoolServerInstance> serverInstanceList = new ArrayList<>(coolServerInstances);
        instanceMap.put(serviceName,coolServerInstances);
        allInstanceList.put(serviceName,serverInstanceList);
    }

    @Override
    public void removeInstances(String serviceName) {
        instanceMap.remove(serviceName);
        allInstanceList.remove(serviceName);
    }

    @Override
    public Set<CoolServerInstance> getInstanceSetByServiceName(String serviceName) {
        return instanceMap.get(serviceName);
    }

    @Override
    public List<CoolServerInstance> getInstanceListByServiceName(String serviceName) {
        return allInstanceList.get(serviceName);
    }

}
