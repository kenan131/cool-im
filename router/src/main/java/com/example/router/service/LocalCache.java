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
    Map<Integer,String> idMap = new ConcurrentHashMap<Integer, String>();
    Map<String, Set<Integer>> ipMap = new ConcurrentHashMap<String, Set<Integer>>();

    public boolean bind(Integer userId,String ip){
        idMap.put(userId,ip);
        Set<Integer> idSet = ipMap.getOrDefault(ip, new HashSet<Integer>());
        idSet.add(userId);
        ipMap.put(ip,idSet);
        return true;
    }

    public boolean unbind(Integer userId,String ip){
        idMap.remove(userId);
        Set<Integer> idSet = ipMap.getOrDefault(ip, new HashSet<Integer>());
        idSet.remove(userId);
        ipMap.put(ip,idSet);
        return true;
    }

    // 服务下线，清空所有在线用户。
    public boolean ServerOff(String ip){
        Set<Integer> ids = ipMap.get(ip);
        for(Integer id : ids){
            idMap.remove(id);
        }
        return true;
    }

    public String getIdByUserId(Integer userId){
        String ip = idMap.get(userId);
        return ip;
    }

}
