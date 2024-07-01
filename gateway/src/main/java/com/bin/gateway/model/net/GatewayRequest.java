package com.bin.gateway.model.net;

import com.bin.gateway.common.GatewayConst;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;

import java.util.Objects;

/**
 * @author: bin
 * @date: 2023/12/21 11:04
 **/

public class GatewayRequest {
    private FullHttpRequest request;

    private HttpHeaders headers;

    private RequestBuilder requestBuilder;
    //请求路径解析器
    private QueryStringDecoder queryStringDecoder;

    private String modifyScheme = GatewayConst.HTTP_PREFIX_SEPARATOR;

    private String modifyHost;

    private String modifyPath;

    private ByteBuf byteBuf;

    public GatewayRequest(FullHttpRequest request, HttpHeaders headers) {
        this.request = request;
        this.headers = headers;
        this.requestBuilder = new RequestBuilder();
        this.queryStringDecoder = new QueryStringDecoder(request.uri());
        this.modifyPath = queryStringDecoder.path();
    }

    public Request buildRequest(){
        requestBuilder.setMethod(getMethod());
        requestBuilder.setHeaders(headers);
        requestBuilder.setQueryParams(queryStringDecoder.parameters());
        requestBuilder.setRequestTimeout(3000); // 设置请求超时时间默认3s
        byteBuf = request.content();
        if(Objects.nonNull(byteBuf)){
            requestBuilder.setBody(byteBuf.nioBuffer());
        }
        requestBuilder.setUrl(getNewUri());
        return requestBuilder.build();
    }

    public void release(){
        //当netty请求处理完毕后释放。
        byteBuf.release();
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public String getNewUri(){
        return modifyScheme+modifyHost+modifyPath;
    }

    public void setModifyHost(String modifyHost) {
        this.modifyHost = modifyHost;
    }

    public void setModifyPath(String modifyPath) {
        this.modifyPath = modifyPath;
    }

    public void addHeadMessage(String name,Object value){
        headers.add(name,value);
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getUniqueId() {
        return headers.get(GatewayConst.UNIQUE_ID);
    }

    public String getPath() {
        return queryStringDecoder.path();
    }

    public String getMethod() {
        return request.method().name();
    }

    public String getHost() {
        return headers.get(HttpHeaderNames.HOST);
    }

    public String getContentType(){
        return HttpUtil.getMimeType(request) == null ? null :
                HttpUtil.getMimeType(request).toString();
    }
}
