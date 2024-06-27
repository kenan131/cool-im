package com.example.router.mq;

import com.bin.api.router.dto.RouterMessageDto;
import com.bin.model.common.MQConstant;
import com.example.router.service.HandlerService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-12
 */
@RocketMQMessageListener(topic = MQConstant.PUSH_TOPIC, consumerGroup = MQConstant.PUSH_GROUP)
@Component
public class PushConsumer implements RocketMQListener<RouterMessageDto> {


    @Autowired
    private HandlerService handlerService;

    @Override
    public void onMessage(RouterMessageDto dto) {
        handlerService.router(dto);
    }
}
