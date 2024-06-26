package com.bin.access.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bin.access.util.WSAdapter;
import com.bin.interfaceapi.access.dto.PushMessageDTO;
import com.bin.interfaceapi.access.dto.WSBaseResp;
import com.bin.interfaceapi.user.UserServiceApi;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: bin.jiang
 * @date: 2024/6/5 15:49
 **/
@Slf4j
@Component
public class WebSocketService {

    private Map<Long, Channel> userIdChannelMap = new HashMap<>();
    private Map<Channel, Long> channelUserIdMap = new HashMap<>();

    @DubboReference(check = false)
    private UserServiceApi userServiceApi;

    public void offLine(ChannelHandlerContext ctx){
        Channel channel = ctx.channel();
        Long userId = channelUserIdMap.get(channel);
        channelUserIdMap.remove(channel);
        userIdChannelMap.remove(userId);
    }

    public void onLine(Long userId,ChannelHandlerContext ctx){
        userIdChannelMap.put(userId,ctx.channel());
        channelUserIdMap.put(ctx.channel(),userId);
    }

    public Channel getChannel(Integer userId){
        Channel channel = userIdChannelMap.get(userId);
        return channel;
    }

    public void connect(String token,ChannelHandlerContext ctx){
        Long userId = userServiceApi.getUserId(token);
        if(userId == null){
            offLine(ctx);
            // 前端token过期，发送消息给前端清理。
            sendMsg(ctx.channel(), WSAdapter.buildInvalidateTokenResp());
        }
        onLine(userId,ctx);
    }

    public void handlerMessage(List<PushMessageDTO> data){
        data.forEach(dto ->{
            dto.getUidList().forEach(uid -> {
                sendToUid(dto.getWsBaseMsg(), uid);
            });
        });
    }


    public void sendToUid(WSBaseResp wsBaseResp, Long uid) {
        Channel channel = userIdChannelMap.get(uid);
        if (channel == null) {
            log.info("用户：{}不在线", uid);
            return;
        }
        sendMsg(channel, wsBaseResp);
    }

    /**
     * 给本地channel发送消息
     */
    public void sendMsg(Channel channel, WSBaseResp wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }




}
