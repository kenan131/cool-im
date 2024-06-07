package com.bin.access.server.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: bin.jiang
 * @date: 2024/6/5 15:49
 **/

public class ServerCache {

    private Map<Integer, Channel> userIdChannelMap = new HashMap<>();
    private Map<Channel, Integer> channelUserIdMap = new HashMap<>();
    public static ServerCache Instance = new ServerCache();


    public void offLine(ChannelHandlerContext ctx){
        Channel channel = ctx.channel();
        Integer userId = channelUserIdMap.get(channel);
        channelUserIdMap.remove(channel);
        userIdChannelMap.remove(userId);

    }

    public void onLine(Integer userId,ChannelHandlerContext ctx){
        userIdChannelMap.put(userId,ctx.channel());
        channelUserIdMap.put(ctx.channel(),userId);
    }
}
