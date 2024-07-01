package com.bin.model.common.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * Description: ws的基本返回信息体
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
public class WSBaseResp<T> implements Serializable {
    /**
     * ws推送给前端的消息
     *
     */
    private Integer type;
    private T data;
}
