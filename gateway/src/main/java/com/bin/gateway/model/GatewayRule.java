package com.bin.gateway.model;

import com.bin.gateway.common.GatewayConst;
import lombok.Data;

/**
 * @author: bin
 * @date: 2023/12/21 14:51
 **/
@Data
public class GatewayRule {
    //负载均衡类型，默认随机
    private String loadBalanceType = GatewayConst.ROUND;
    //是否需要认证，默认需要
    private Boolean isAuthority = false;
    //重试异常后，是否允许更换实例地址。
    private Boolean isReplaceInstance = true;
    //重试次数
    private int tryNumber = 3;
    //是否需要限流
    private Boolean isLimit = false;
    //是否灰度请求
    private Boolean isGray = false;
}
