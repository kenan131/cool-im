package com.bin.gateway.model.net;

import com.alibaba.fastjson.JSON;
import com.bin.gateway.common.ResponseCode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.Data;
import org.asynchttpclient.Response;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author: bin
 * @date: 2023/12/22 15:48
 **/
@Data
public class GatewayResponseRes implements Serializable {

    private static final long serialVersionUID = -8263363465897965189L;

    private int gatewayCode;
    private String message;
    private Object data;

    public static FullHttpResponse builderGatewayRes(Response response, ResponseCode responseCode){
        DefaultFullHttpResponse httpResponse;
        GatewayResponseRes gatewayResponseRes = new GatewayResponseRes();
        HttpResponseStatus httpResponseStatus;
        HttpHeaders headers;
        gatewayResponseRes.setGatewayCode(responseCode.getCode());
        gatewayResponseRes.setMessage(responseCode.getMessage());
        if(response == null){
            httpResponseStatus = responseCode.getStatus();
            headers = new DefaultHttpHeaders();
            headers.add(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.APPLICATION_JSON+";charset=utf-8");
        }
        else{
            httpResponseStatus = HttpResponseStatus.valueOf(response.getStatusCode());
            headers = response.getHeaders();
            //因网关层包装了返回信息，故设置json串格式。
            headers.set(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.APPLICATION_JSON+";charset=utf-8");
            //有返回内容，赋值给data
            gatewayResponseRes.setData(response.getResponseBody(StandardCharsets.UTF_8));
        }
        ByteBuf content = Unpooled.wrappedBuffer(JSON.toJSONBytes(gatewayResponseRes));
        httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,httpResponseStatus,content);
        httpResponse.headers().add(headers);
        return httpResponse;
    }

    public static FullHttpResponse builderGatewayFailRes(Response response, ResponseCode responseCode){
        DefaultFullHttpResponse httpResponse;
        GatewayResponseRes gatewayResponseRes = new GatewayResponseRes();
        gatewayResponseRes.setGatewayCode(responseCode.getCode());
        gatewayResponseRes.setMessage(responseCode.getMessage());
        gatewayResponseRes.setData(response.getResponseBody(StandardCharsets.UTF_8));
        HttpResponseStatus httpResponseStatus = HttpResponseStatus.valueOf(response.getStatusCode());
        HttpHeaders headers = response.getHeaders();
        headers.remove("Transfer-Encoding");
        headers.set(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.APPLICATION_JSON+";charset=utf-8");
        ByteBuf content = Unpooled.wrappedBuffer(JSON.toJSONBytes(gatewayResponseRes));
        httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,httpResponseStatus,content);
        httpResponse.headers().add(headers);
        return httpResponse;
    }

    public static FullHttpResponse builderGatewayNativeRes(Response response, ResponseCode responseCode){
        DefaultFullHttpResponse httpResponse;
        HttpResponseStatus httpResponseStatus;
        HttpHeaders headers;
        if(response == null){
            httpResponseStatus = responseCode.getStatus();
            headers = new DefaultHttpHeaders();
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,httpResponseStatus);
        }
        else{
            httpResponseStatus = HttpResponseStatus.valueOf(response.getStatusCode());
            headers = response.getHeaders();
            //有返回内容，赋值给data
            ByteBuf content = Unpooled.wrappedBuffer(response.getResponseBodyAsBytes());
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,httpResponseStatus,content);
        }
        httpResponse.headers().add(headers);
        return httpResponse;
    }
}
