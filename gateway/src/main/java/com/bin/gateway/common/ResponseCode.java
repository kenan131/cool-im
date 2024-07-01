package com.bin.gateway.common;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;

/**
 * @author: bin
 * @date: 2023/12/20 10:54
 **/
@Getter
public enum ResponseCode {

    SUCCESS(HttpResponseStatus.OK,10001,"调用成功"),
    GATEWAY_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR,10002,"网关内部发生异常"),
    INSTANCE_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR,10003,"请求服务实例，发生异常"),
    SERVICE_INSTANCE_NOT_FOUND(HttpResponseStatus.NOT_FOUND,10004,"配置文件有误or实例未注册，根据服务名未找到注册实例地址"),
    PATH_MATCH_NOT_FOUND(HttpResponseStatus.NOT_FOUND,10005,"配置文件有误，根据请求路径未找到实例名"),
    REQUEST_TIMEOUT(HttpResponseStatus.GATEWAY_TIMEOUT, 10006, "连接下游服务超时or连接IO异常"),
    REQUEST_FAIL(HttpResponseStatus.SERVICE_UNAVAILABLE, 10007, "下游服务请求失败，请看数据信息"),
    REQUEST_LIMIT(HttpResponseStatus.TOO_MANY_REQUESTS, 10008, "当前ip请求速度过快，网关执行限流逻辑"),
    AUTHORITY_FAIL(HttpResponseStatus.UNAUTHORIZED, 10009, "用户未登录，请登录后再访问");

    private HttpResponseStatus status;
    private int code;
    private String message;

    ResponseCode(HttpResponseStatus status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
