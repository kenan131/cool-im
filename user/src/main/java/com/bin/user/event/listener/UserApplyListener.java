package com.bin.user.event.listener;

import com.bin.api.access.dto.WSAdapter;
import com.bin.model.user.vo.response.ws.WSFriendApply;
import com.bin.user.dao.UserApplyDao;
import com.bin.model.user.entity.UserApply;
import com.bin.user.event.UserApplyEvent;
import com.bin.user.mq.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;

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

    @Autowired
    private PushService pushService;

    @Async
    @EventListener(classes = UserApplyEvent.class)
    public void notifyFriend(UserApplyEvent event) {
        try{
            UserApply userApply = event.getUserApply();
            Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
            pushService.sendPushMsg(WSAdapter.buildApplySend(new WSFriendApply(userApply.getUid(), unReadCount)), Collections.singletonList(userApply.getTargetId()));
        }catch (Exception e){
            e.printStackTrace();
            log.error("好友通知失败"+event.toString());
        }
    }

}
