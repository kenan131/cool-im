package com.bin.user.event.listener;

import com.bin.interfaceapi.access.AccessServiceApi;
import com.bin.interfaceapi.router.RouterServiceApi;
import com.bin.interfaceapi.router.dto.RouterMessageDto;
import com.bin.interfaceapi.router.dto.RouterMessageEnum;
import com.bin.user.dao.UserApplyDao;
import com.bin.user.domain.entity.UserApply;
import com.bin.user.domain.enums.WSBaseResp;
import com.bin.user.domain.ws.WSFriendApply;
import com.bin.user.event.UserApplyEvent;
import com.bin.user.mq.PushService;
import com.bin.user.mq.dto.PushMessageDTO;
import com.bin.user.utils.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 好友申请监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class UserApplyListener {
    @Autowired
    private UserApplyDao userApplyDao;

    @DubboReference(check = false)
    private RouterServiceApi routerServiceApi;


    @Async
    @EventListener(classes = UserApplyEvent.class)
    public void notifyFriend(UserApplyEvent event) {
        try{
            UserApply userApply = event.getUserApply();
            Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
            WSBaseResp<WSFriendApply> msg = WSAdapter.buildApplySend(new WSFriendApply(userApply.getUid(), unReadCount));
            PushMessageDTO data = new PushMessageDTO(userApply.getTargetId(), msg);
            RouterMessageDto routerMessageDto = new RouterMessageDto(RouterMessageEnum.DiRECT_PUSH.getType(),userApply.getTargetId(),null,data);
            routerServiceApi.router(routerMessageDto);
        }catch (Exception e){
            e.printStackTrace();
            log.error("好友通知失败"+event.toString());
        }
    }

}
