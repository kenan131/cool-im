package com.bin.interfaceapi.router.dto;

import com.bin.interfaceapi.access.dto.PushMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: bin.jiang
 * @date: 2024/6/24 20:43
 **/
@Data
public class RouterMessageDto implements Serializable {
    /*
        路由消息类型，目前主要是区分，转发消息和聚合消息
     */
    Integer type;
    /*
        接收者消息id
     */
    Long receiveId;
    /*
        房间id
     */
    Long roomId;
    /*
        转发类型的数据为
     */
    Object data;

    public RouterMessageDto(Integer type, Long receiveId, Long roomId, Object data) {
        this.type = type;
        this.receiveId = receiveId;
        this.roomId = roomId;
        this.data = data;
    }
}
