package com.bin.gateway.net.client;

import com.bin.gateway.net.LifeCycle;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author: bin
 * @date: 2023/12/18 15:48
 **/
@Slf4j
public class NettyClient extends LifeCycle {

    private static EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    // 异步HTTP客户端实例
    private AsyncHttpClient asyncHttpClient;

    static class SingletonInstance{
        private static final NettyClient Instance = new NettyClient();
    }
    public static NettyClient getInstance(){
        return SingletonInstance.Instance;
    }

    public NettyClient() {
        init();
    }

    @Override
    public void init() {
        // 创建异步HTTP客户端配置的构建器
        DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder()
                .setEventLoopGroup(eventLoopGroup) // 使用传入的Netty事件循环组
                .setAllocator(PooledByteBufAllocator.DEFAULT) // 使用池化的ByteBuf分配器以提升性能
                .setCompressionEnforced(true);// 强制压缩
        // 根据配置创建异步HTTP客户端
        this.asyncHttpClient = new DefaultAsyncHttpClient(builder.build());
    }


    public CompletableFuture<Response> sendRequest(Request request){
        ListenableFuture<Response> responseListenableFuture = asyncHttpClient.executeRequest(request);
        CompletableFuture<Response> responseCompletableFuture = responseListenableFuture.toCompletableFuture();
        return responseCompletableFuture;
    }

    @Override
    public void start() {
        super.onStart();
    }

    @Override
    public void stop() {
        // 如果客户端实例不为空，则尝试关闭它
        if (asyncHttpClient != null) {
            try {
                // 关闭客户端，并处理可能的异常
                this.asyncHttpClient.close();
            } catch (IOException e) {
                // 记录关闭时发生的错误
                log.error("NettyHttpClient shutdown error", e);
            }
        }
        super.onStop();
    }
}
