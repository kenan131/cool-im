package com.bin.api.router.dto;

import com.bin.model.common.exception.WSBaseResp;
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

    public RouterMessageDto() {
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Long> getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(List<Long> receiveId) {
        this.receiveId = receiveId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public WSBaseResp<?> getData() {
        return data;
    }

    public void setData(WSBaseResp<?> data) {
        this.data = data;
    }
}
