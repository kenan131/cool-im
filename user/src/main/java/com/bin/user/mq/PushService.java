package com.bin.user.mq;

import com.bin.api.router.dto.RouterMessageDto;
import com.bin.api.router.dto.RouterMessageEnum;
import com.bin.model.common.MQConstant;
import com.bin.model.user.dto.PushMessageDTO;
import com.bin.model.user.enums.WSBaseResp;
import com.bin.transaction.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-12
 */
@Service
public class PushService {
    @Autowired
    private MQProducer mqProducer;

    public void sendPushMsg(WSBaseResp<?> msg, List<Long> uidList) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(uidList, msg));
    }

    public void sendPushMsg(WSBaseResp<?> msg, Long uid) {
        // 给路由层的dto
        RouterMessageDto routerMessageDto = new RouterMessageDto(RouterMessageEnum.DiRECT_PUSH.getType(), Collections.singletonList(uid),null,msg);
        // 将路由层的dto推送。
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, routerMessageDto);
    }

    public void sendPushRoomMsg(WSBaseResp<?> msg,Long roomId) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(msg));
    }

    public void sendPushMsg(WSBaseResp<?> msg) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(msg));
    }
}
