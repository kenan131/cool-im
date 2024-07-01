package com.bin.gateway.filter;

import com.bin.gateway.common.CoolGateWayException;
import com.bin.gateway.common.ResponseCode;
import com.bin.gateway.filter.loadBalance.LoadBalanceFilter;
import com.bin.gateway.model.Context;
import com.bin.gateway.model.net.GatewayResponseRes;
import com.bin.gateway.net.client.NettyClient;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

/**
 * @author: bin
 * @date: 2023/12/20 10:15
 **/
@Slf4j
@Component
public class RouterFilter implements Filter{

    @Autowired
    private LoadBalanceFilter loadBalanceFilter;

    @Override
    public void doFiler(Context context) {
        try{
            Request request = context.getGatewayRequest().buildRequest();
            CompletableFuture<Response> future = NettyClient.getInstance().sendRequest(request);
            future.whenCompleteAsync((response, throwable) -> {
                processRes(context,response,throwable);
            });
        }catch (Exception e){
            log.error("routerFilter trigger error:",e);
            throw new CoolGateWayException(ResponseCode.INSTANCE_ERROR);
        }
    }

    public void processRes(Context context,Response response,Throwable throwable){
        FullHttpResponse fullHttpResponse = null;
        try{
            //将请求次数加1
            context.getGatewayResponse().addRequestNumber();
            if(throwable != null){
                if(context.getGatewayRule().getTryNumber() == context.getGatewayResponse().getRequestNumber()){
                    //请求次数已满
                    if(throwable instanceof TimeoutException || throwable instanceof IOException){
                        fullHttpResponse = GatewayResponseRes.builderGatewayRes(null, ResponseCode.REQUEST_TIMEOUT);
                    }else{
                        fullHttpResponse = GatewayResponseRes.builderGatewayRes(null, ResponseCode.INSTANCE_ERROR);
                    }
                    context.getGatewayResponse().setFullHttpResponse(fullHttpResponse);
                }else {
                    if (context.getGatewayRule().getIsReplaceInstance()) {
                        //重新选择实例地址。
                        loadBalanceFilter.doFiler(context);
                    }
                    this.doFiler(context);
                    return;
                }
            } else{
                //请求未报异常。
                context.getGatewayResponse().setResponse(response);
                if(response.getStatusCode() == HttpResponseStatus.OK.code()){
                    fullHttpResponse = GatewayResponseRes.builderGatewayRes(response, ResponseCode.SUCCESS);
                }else{
                    fullHttpResponse = GatewayResponseRes.builderGatewayFailRes(response, ResponseCode.REQUEST_FAIL);
                }
                context.getGatewayResponse().setFullHttpResponse(fullHttpResponse);
            }
        }catch (CoolGateWayException e){
            log.error(e.getResponseCode().getMessage());
            fullHttpResponse = GatewayResponseRes.builderGatewayRes(null, e.getResponseCode());
            context.getGatewayResponse().setFullHttpResponse(fullHttpResponse);
        }
        catch (Exception e) {
            log.error("RouterFilter 服务回调函数发生唯一异常:" + e);
            fullHttpResponse = GatewayResponseRes.builderGatewayRes(null, ResponseCode.INSTANCE_ERROR);
            context.getGatewayResponse().setFullHttpResponse(fullHttpResponse);
        }finally {
            context.getCtx().writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
            context.getGatewayRequest().getRequest().release();
        }
    }
    @Override
    public int getOrder() {
        return 25;
    }
}
