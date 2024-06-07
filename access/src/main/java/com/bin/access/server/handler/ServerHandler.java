package com.bin.access.server.handler;

import cn.hutool.core.util.StrUtil;
import com.bin.access.api.UserApi;
import com.bin.access.server.service.ServerCache;
import com.bin.access.util.NettyUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: bin.jiang
 * @date: 2024/6/5 15:37
 **/

@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private ServerCache serverCache = ServerCache.Instance;

    private ThreadPoolExecutor executor;

    public ServerHandler(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text = textWebSocketFrame.text();
        System.out.println(text);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 关闭用户的连接
                serverCache.offLine(ctx);
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            System.out.println(token);
            if (StrUtil.isNotBlank(token)) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Integer userId = UserApi.getUserId(token);
                        if(userId == null){
                            serverCache.offLine(ctx);
                            // 前端token过期，发送消息给前端清理。
                            // TODO
                        }
                        serverCache.onLine(userId,ctx);
//                        ctx.writeAndFlush("你哈！").addListener(ChannelFutureListener.CLOSE);
                    }
                });
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
        serverCache.offLine(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("异常发生，异常消息 ={}", cause);
        serverCache.offLine(ctx);
        ctx.channel().close();
    }

}
