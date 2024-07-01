package com.bin.gateway.process;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author: bin
 * @date: 2023/12/19 17:37
 **/

public abstract class AbstractProcess {
    public abstract void process(FullHttpRequest request, ChannelHandlerContext ctx);
}
