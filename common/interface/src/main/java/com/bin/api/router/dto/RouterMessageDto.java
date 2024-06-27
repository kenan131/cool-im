package com.bin.api.router.dto;

import com.bin.model.user.enums.WSBaseResp;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: bin.jiang
 * @date: 2024/6/24 20:43
 **/
@Data
@Builder
public class RouterMessageDto implements Serializable {
    /*
        路由消息类型，目前主要是区分，转发消息和聚合消息
     */
    Integer type;
    /*
        接收者消息id
     */
    List<Long> receiveId;
    /*
        房间id
     */
    Long roomId;
    /*
        转发类型的数据为
     */
    WSBaseResp<?> data;

    public RouterMessageDto(Integer type, List<Long> receiveId, Long roomId, WSBaseResp<?> data) {
        this.type = type;
        this.receiveId = receiveId;
        this.roomId = roomId;
        this.data = data;
    }
}
