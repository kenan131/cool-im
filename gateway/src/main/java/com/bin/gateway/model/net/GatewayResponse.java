package com.bin.gateway.model.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.Data;
import org.asynchttpclient.Response;

/**
 * @author: bin
 * @date: 2023/12/21 11:07
 **/
@Data
public class GatewayResponse {
    //原请求结果
    private Response response;
    //报错信息
    private Throwable throwable;
    //构建的新返回结果
    private FullHttpResponse fullHttpResponse;
    //请求次数
    private int requestNumber = 0;

    public void addRequestNumber(){
        requestNumber++;
    }

    //构建返回response
    public static FullHttpResponse buildFullHttpResponse(Response response){
        ByteBuf content = null;
        try{
            content = Unpooled.wrappedBuffer(response.getResponseBodyAsByteBuffer());
            DefaultFullHttpResponse httpResponse =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.getStatusCode()),content);
            httpResponse.headers().add(response.getHeaders());
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
            return httpResponse;
        }finally {
            if(content != null){
                content.release();
            }
        }
    }
}
