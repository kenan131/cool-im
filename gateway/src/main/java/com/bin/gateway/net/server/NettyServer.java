package com.bin.gateway.net.server;

import com.bin.gateway.net.LifeCycle;
import com.bin.gateway.process.AbstractProcess;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author: bin
 * @date: 2023/12/18 17:23
 **/
@Slf4j
public class NettyServer extends LifeCycle {

    private int port;

    private int coreThreadNumber;

    private int maxThreadNumber;
    //启动线程
    private Thread startThread;

    private ServerBootstrap serverBootstrap;

    // boss线程组，用于处理新的客户端连接
    private EventLoopGroup eventLoopGroupBoss;

    // worker线程组，用于处理已经建立的连接的后续操作
    private EventLoopGroup eventLoopGroupWoke;

    //自定义线程池处理后续流程
    private ThreadPoolExecutor processThreadPool;

    private AbstractProcess process;

    public NettyServer(int port, int coreThreadNumber, int maxThreadNumber, AbstractProcess process) {
        this.port = port;
        this.coreThreadNumber = coreThreadNumber;
        this.maxThreadNumber = maxThreadNumber;
        this.process = process;
        init();
    }

    @Override
    public void init() {
        serverBootstrap = new ServerBootstrap();
        eventLoopGroupBoss = new NioEventLoopGroup();
        eventLoopGroupWoke = new NioEventLoopGroup();
        processThreadPool = new ThreadPoolExecutor(
                coreThreadNumber,
                maxThreadNumber,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "cool-"+"serverHandlerPool-" + r.hashCode());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        throw new RuntimeException("处理线程池执行拒绝策略。");
                    }
                });
        startThread = new Thread(new Runnable() {
            @Override
            public void run() {
                serverBootstrap
                        .group(eventLoopGroupBoss,eventLoopGroupWoke)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_REUSEADDR, true)          // 允许端口重用
                        .option(ChannelOption.SO_KEEPALIVE, true)          // 保持连接检测
                        .childOption(ChannelOption.TCP_NODELAY, true)      // 禁用Nagle算法，适用于小数据即时传输
                        .childHandler(new ChannelInitializer<Channel>(){
                            @Override
                            protected void initChannel(Channel channel) throws Exception {
                                channel.pipeline().addLast(
                                        new HttpServerCodec(), // 处理HTTP请求的编解码器
                                        new HttpObjectAggregator(65536), // 聚合HTTP请求
                                        new HttpServerExpectContinueHandler(), // 处理HTTP 100 Continue请求
                                        new NettyServerHandler(processThreadPool,process) // 连接管理处理器
                                );}
                        });
                try {
                    log.info("server started on port:"+port);
                    ChannelFuture future = serverBootstrap.bind(port).sync();
                    future.channel().closeFuture().sync();
                } catch (Exception e) {
                    if (e instanceof InterruptedException) {
                        //触发中断异常，结束启动线程。
                        log.info("netty server stop.");
                    } else {
                        log.error("netty server error.", e);
                    }
                }finally {
                    try {
                        //关闭线程池
                        processThreadPool.shutdown();
                        //释放netty资源
                        eventLoopGroupBoss.shutdownGracefully();
                        eventLoopGroupWoke.shutdownGracefully();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        });
    }

    @Override
    public void start() {
        startThread.start();
        super.onStart();
    }

    @Override
    public void stop() {
        if(startThread != null && startThread.isAlive()){
            startThread.interrupt();
        }
        super.onStop();
    }
}
