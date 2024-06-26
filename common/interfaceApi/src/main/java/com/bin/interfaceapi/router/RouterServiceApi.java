package com.bin.interfaceapi.router;

import com.bin.interfaceapi.router.dto.RouterMessageDto;

/**
 * @author: bin.jiang
 * @date: 2024/6/25 15:07
 **/

public interface RouterServiceApi {

    // 用户上线，用户id和接入层ip绑定
    public boolean bind(Integer userId,String ip);

    // 用户下线，解绑
    public boolean unbind(Integer userId,String ip);

    // 服务下线，清空所有在线用户。
    public boolean ServerOff(String ip);

    // 消息路由
    public boolean router(RouterMessageDto dto);


}
