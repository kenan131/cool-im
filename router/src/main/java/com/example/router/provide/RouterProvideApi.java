package com.example.router.provide;

import com.bin.interfaceapi.router.dto.RouterMessageDto;
import com.bin.interfaceapi.router.RouterServiceApi;
import com.example.router.service.HandlerService;
import com.example.router.service.LocalCache;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: bin.jiang
 * @date: 2024/6/24 10:13
 **/
@Component
@DubboService
public class RouterProvideApi implements RouterServiceApi {

    @Autowired
    private LocalCache localCache;

    @Autowired
    private HandlerService handlerService;


    public boolean bind(Integer userId,String ip){
        localCache.bind(userId,ip);
        return true;
    }

    public boolean unbind(Integer userId,String ip){
        localCache.unbind(userId,ip);
        return true;
    }

    // 服务下线，清空所有在线用户。
    public boolean ServerOff(String ip){
        localCache.ServerOff(ip);
        return true;
    }

    public boolean router(RouterMessageDto dto){
        handlerService.router(dto);
        return true;
    }

}
