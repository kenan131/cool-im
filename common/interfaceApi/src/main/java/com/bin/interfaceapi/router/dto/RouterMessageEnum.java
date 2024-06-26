package com.bin.interfaceapi.router.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: bin.jiang
 * @date: 2024/6/24 20:59
 **/
@AllArgsConstructor
@Getter
public enum RouterMessageEnum {

    DiRECT_PUSH(1,"直接推送"),
    MESSAGE_AGGREGATION(2,"消息聚合"),
    ;

    private final Integer type;
    private final String name;
}
