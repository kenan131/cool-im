package com.example.router.service;

import cn.hutool.core.util.StrUtil;
import com.bin.api.access.dto.WSAdapter;
import com.bin.api.router.dto.RouterMessageDto;
import com.bin.api.access.AccessServiceApi;
import com.bin.api.im.ImServiceApi;
import com.bin.api.router.dto.RouterMessageEnum;
import com.bin.model.user.dto.PushMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.cluster.specifyaddress.Address;
import org.apache.dubbo.rpc.cluster.specifyaddress.UserSpecifiedAddressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author: bin.jiang
 * @date: 2024/6/24 21:24
 **/
@Component
@Slf4j
public class HandlerService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ConcurrentHashMap<Long, Set<Object>> messageCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(this::aggregateAndPushMessages, 0, 1, TimeUnit.SECONDS);
    }

    private final ThreadPoolExecutor aggregateThread = new ThreadPoolExecutor(4, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 自身线程执行
            r.run();
        }
    });

    @Autowired
    private LocalCache localCache;

    // dubbo服务提供者的默认端口号。
    @Value("${access.dubbo.port}")
    private Integer dubboPort;

    @DubboReference(check = false, cluster = "broadcast", async = true)
    private AccessServiceApi accessServiceApi;

    @DubboReference(check = false)
    private ImServiceApi imServiceApi;

    public void router(RouterMessageDto dto) {
        if (dto.getType().equals(RouterMessageEnum.DiRECT_PUSH.getType())) {
            try {
                for(Long userId : dto.getReceiveId()){
                    String accessIp = localCache.getIdByUserId(userId);
                    // 指定ip实现推送。
                    if(StrUtil.isEmpty(accessIp)){
                        // 用户不在线，跳过
                        continue;
                    }
                    ReferenceConfig<AccessServiceApi> config = new ReferenceConfig<>();
                    config.setInterface(AccessServiceApi.class);
                    config.setAsync(true);
                    AccessServiceApi demoService1 = config.get();
                    System.out.println(accessIp);
                    String ip = accessIp;
                    UserSpecifiedAddressUtil.setAddress(new Address(ip, dubboPort, true));
                    PushMessageDTO pushMessageDTO = new PushMessageDTO(userId, dto.getData());
                    demoService1.pushMessage(Collections.singletonList(pushMessageDTO));
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Dubbo推送指定IP服务失败,报错信息 " + e.getMessage());
            }
        } else if (dto.getType().equals(RouterMessageEnum.MESSAGE_AGGREGATION.getType())) {
            // 消息聚合
            onNewMessageReceived(dto.getRoomId(), dto.getData());
        }
    }

    public void onNewMessageReceived(Long groupId, Object data) {
        // 收到新消息时，更新缓存
        messageCache.compute(groupId, (key, value) -> {
            if (value == null) {
                HashSet<Object> set = new HashSet<>();
                set.add(data);
                return set;
            } else {
                value.add(data);
                return value;
            }
        });
    }

    private void aggregateAndPushMessages() {
        try {
            ConcurrentHashMap<Long, Set<Object>> tempMap = null;
            synchronized (messageCache) {
                tempMap = new ConcurrentHashMap<>(messageCache);
                messageCache.clear();
            }
            ConcurrentHashMap<Long, Set<Object>> finalTempMap = tempMap;
            if (finalTempMap.size() == 0)
                return;
            aggregateThread.execute(new Runnable() {
                @Override
                public void run() {
                    handler(finalTempMap);
                }
            });
        } catch (Exception e) {
            log.error("定时线程池发生未知异常" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handler(ConcurrentHashMap<Long, Set<Object>> tempMap) {
        System.out.println("消息聚合开始！" );
        System.out.println(tempMap);
        try {
            ArrayList<PushMessageDTO> pushMessages = new ArrayList<>();
            List<Long> roomIds = tempMap.keySet().stream().collect(Collectors.toList());
            Map<Long, List<Long>> memberMap = imServiceApi.getMemberIdsByRoomIds(roomIds);
            System.out.println("房间需要推送的用户id" + memberMap);
            tempMap.forEach((roomId, set) -> {
                List<Object> data = set.stream().collect(Collectors.toList());
                // 这里得查询出 房间所有成员id
                List<Long> ids = memberMap.get(roomId);
                PushMessageDTO pushMessageDTO = new PushMessageDTO(ids, WSAdapter.buildMsgSend(data));
                // 推送一个房间的聚合消息
                pushMessages.add(pushMessageDTO);
            });
            // 推送所有房间的聚合消息。
            accessServiceApi.pushMessage(pushMessages);
            tempMap.clear();
        } catch (Exception e) {
            log.error("消息聚合失败" + e.getMessage());
            e.printStackTrace();
        }
    }

}
