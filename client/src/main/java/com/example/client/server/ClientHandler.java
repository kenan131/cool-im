package com.example.client.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: bin.jiang
 * @date: 2024/6/5 17:04
 **/

@ChannelHandler.Sharable
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE){
                ctx.channel().writeAndFlush(new TextWebSocketFrame("你好！")).addListener( future -> {
                    if (!future.isSuccess()) {
                        Throwable cause = future.cause();
                        System.out.println(cause.getMessage());
                        System.out.println("发送失败！");
                        ctx.close();
                    }else
                        System.out.println("发送成功！");
                });
            }

        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

    }
}
