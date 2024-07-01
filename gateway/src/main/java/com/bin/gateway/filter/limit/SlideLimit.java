package com.bin.gateway.filter.limit;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: bin
 * @date: 2023/12/27 11:43
 **/

public class SlideLimit extends AbstractLimit{

    // 滑动窗口，ip : 双端队列
    private Map<String,Deque<Long>> map = new ConcurrentHashMap<>();


    private Long timePoint = 0l;

    @Override
    public synchronized boolean doLimit(String ip) {
        long currentTimeMillis = System.currentTimeMillis();
        if(currentTimeMillis > timePoint){
            map.clear();
            timePoint = currentTimeMillis + (1000*60*60*24);//一天后清空。
        }
        Deque<Long> queue = map.get(ip);
        if(queue == null){
            queue = new ArrayDeque<>();
            queue.addLast(currentTimeMillis);
            map.put(ip,queue);
            return true;
        }
        boolean flag = false;
        while(queue.size()!=0 && currentTimeMillis- timeWindow> queue.getFirst()){
            // poll 出时间窗前的时间点。
            queue.pollFirst();
        }
        if(queue.size()+1 <= qps){
            //如果窗口队列中数量小于等于qps 则放行
            queue.addLast(currentTimeMillis);
            flag = true;
        }
        map.put(ip,queue);
        return flag;
    }

}
