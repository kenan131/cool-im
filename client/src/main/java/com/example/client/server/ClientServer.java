package com.example.client.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author: bin.jiang
 * @date: 2024/6/5 16:36
 **/
@Component
@Slf4j
public class ClientServer {


    private Channel channel;

    @PostConstruct
    public void init() throws Exception {
        run();
    }

    public void run() throws URISyntaxException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup(0, new DefaultThreadFactory("cim-work"));
        final URI webSocketURL = new URI("ws://127.0.0.1:8081/socket?token=1");
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new IdleStateHandler(0, 3, 0))
                                .addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(512 * 1024))
                                .addLast(new WebSocketClientProtocolHandler(WebSocketClientHandshakerFactory.newHandshaker(webSocketURL, WebSocketVersion.V13, null, false, new DefaultHttpHeaders())))
                                .addLast(new ClientHandler());
                    }
                });
        ChannelFuture future = null;
        try {
            future = bootstrap.connect("127.0.0.1", 8081).sync();
        } catch (Exception e) {
            System.out.println("客户端连接失败");
        }
        if (future.isSuccess()) {
            System.out.println("启动成功！");
        }
        channel = (SocketChannel) future.channel();

        try {
            Thread.sleep(1000);
//            String str = "你好！";
//            channel.writeAndFlush(str).addListeners((ChannelFutureListener) ff -> {
//                if (!ff.isSuccess()) {
//                    ff.channel().close();
//                }
//                System.out.println("发送成功！");
//            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
