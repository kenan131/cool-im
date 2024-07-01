package com.bin.im.mq;

import com.bin.api.router.dto.RouterMessageDto;
import com.bin.api.router.dto.RouterMessageEnum;
import com.bin.model.common.MQConstant;
import com.bin.model.user.dto.PushMessageDTO;
import com.bin.model.common.exception.WSBaseResp;
import com.bin.transaction.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // 给路由层的dto
        RouterMessageDto routerMessageDto = new RouterMessageDto(RouterMessageEnum.DiRECT_PUSH.getType(),uidList,null,msg);
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, routerMessageDto);
    }

    public void sendPushRoomMsg(WSBaseResp<?> msg, Long roomId) {
        RouterMessageDto routerMessageDto = new RouterMessageDto(RouterMessageEnum.MESSAGE_AGGREGATION.getType(),null,roomId,msg);
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, routerMessageDto);
    }

    public void sendSecureMsg(String topic, Object body, Object key) {
        mqProducer.sendSecureMsg(topic,body,key);
    }
}
