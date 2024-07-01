package com.bin.gateway.filter.limit;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: bin
 * @date: 2023/12/27 13:43
 **/
@Slf4j
public class CountLimit extends AbstractLimit{

    private Map<String,Integer> map = new HashMap<>();

    private Long timePoint = 0l;

    public CountLimit() {}

    @Override
    public boolean doLimit(String ip) {
        synchronized (map){
            if(System.currentTimeMillis() > timePoint){
                map.clear();
                timePoint = System.currentTimeMillis() + timeWindow;
            }
            Integer cnt = map.get(ip);
            if(cnt == null){
                map.put(ip,1);
                return true;
            }
            if(cnt+1>qps) {
                return false;
            }
            map.put(ip,cnt+1);
            return true;
        }
    }
}
