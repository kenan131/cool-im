package com.bin.interfaceapi.access.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Description: ws的基本返回信息体
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
public class WSBaseResp implements Serializable {

    private Integer type;
    private List<Object> data;
}
