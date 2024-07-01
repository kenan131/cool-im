package com.bin.gateway.net.server;

import com.bin.gateway.process.AbstractProcess;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: bin
 * @date: 2023/12/19 11:40
 **/
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    private ThreadPoolExecutor processThreadPool;

    private AbstractProcess process;

    public NettyServerHandler(ThreadPoolExecutor processThreadPool,AbstractProcess process) {
        this.processThreadPool = processThreadPool;
        this.process = process;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        // 使用处理线程池执行后续流程。
        processThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                process.process(request,ctx);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("netty serverHandler trigger exception. errorMessage:"+cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }
}
