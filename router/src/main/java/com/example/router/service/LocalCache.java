package com.example.router.service;

import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: bin.jiang
 * @date: 2024/6/24 21:20
 **/
@Component
public class LocalCache {

    // 这里的状态信息应该做在redis中，这样就可以实现服务扩容。
    Map<Long,String> idMap = new ConcurrentHashMap<>();
    Map<String, Set<Long>> ipMap = new ConcurrentHashMap<>();

    public boolean bind(Long userId,String ip){
        System.out.println(userId + " : " + ip);
        idMap.put(userId,ip);
        Set<Long> idSet = ipMap.getOrDefault(ip, new HashSet<Long>());
        idSet.add(userId);
        ipMap.put(ip,idSet);
        return true;
    }

    public boolean unbind(Long userId,String ip){
        idMap.remove(userId);
        Set<Long> idSet = ipMap.getOrDefault(ip, new HashSet<Long>());
        idSet.remove(userId);
        ipMap.put(ip,idSet);
        return true;
    }

    // 服务下线，清空所有在线用户。
    public boolean ServerOff(String ip){
        Set<Long> ids = ipMap.get(ip);
        if(ids != null){
            for(Long id : ids){
                idMap.remove(id);
            }
        }
        return true;
    }

    public String getIdByUserId(Long userId){
        String ip = idMap.get(userId);
        return ip;
    }

}
