package com.bin.gateway.model;

import com.bin.gateway.model.net.GatewayRequest;
import com.bin.gateway.model.net.GatewayResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

/**
 * @author: bin
 * @date: 2023/12/19 17:46
 **/
@Data
public class Context {

    //封装网关请求
    private GatewayRequest gatewayRequest;
    //封装网关结果
    private GatewayResponse gatewayResponse;
    //网关配置
    private GatewayRule gatewayRule;
    //服务名
    private String serviceName;
    //netty请求上下文
    private ChannelHandlerContext ctx;

    public Context(FullHttpRequest request,ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.gatewayRequest = new GatewayRequest(request,request.headers());
        this.gatewayRule = new GatewayRule();
        this.gatewayResponse = new GatewayResponse();
    }
}
