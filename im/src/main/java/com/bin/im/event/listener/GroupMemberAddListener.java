package com.bin.im.event.listener;

import com.bin.im.api.IUserService;
import com.bin.im.dao.GroupMemberDao;
import com.bin.im.domain.entity.RoomGroup;
import com.bin.im.domain.entity.GroupMember;
import com.bin.model.user.entity.User;
import com.bin.im.domain.vo.request.ChatMessageReq;
import com.bin.im.domain.ws.WSMemberChange;
import com.bin.im.event.GroupMemberAddEvent;
import com.bin.im.mq.PushService;
import com.bin.im.mq.dto.WSBaseResp;
import com.bin.im.service.ChatService;
import com.bin.im.service.adapter.MemberAdapter;
import com.bin.im.service.adapter.RoomAdapter;
import com.bin.im.service.cache.imp.GroupMemberCache;
import com.bin.im.service.cache.imp.MsgCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 添加群成员监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class GroupMemberAddListener {
    @Autowired
    private ChatService chatService;
    @Autowired
    private MsgCache msgCache;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private GroupMemberDao groupMemberDao;
    @DubboReference(interfaceClass = IUserService.class)
    private IUserService IUserService;
    @Autowired
    private GroupMemberCache groupMemberCache;
    @Autowired
    private PushService pushService;


    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendAddMsg(GroupMemberAddEvent event) {
        List<GroupMember> memberList = event.getMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        Long inviteUid = event.getInviteUid();
        User user = IUserService.get(inviteUid);
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        ChatMessageReq chatMessageReq = RoomAdapter.buildGroupAddMessage(roomGroup, user, IUserService.getBatch(uidList));
        chatService.sendMsg(chatMessageReq, User.UID_SYSTEM);
    }

    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendChangePush(GroupMemberAddEvent event) {
        List<GroupMember> memberList = event.getMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        List<User> users = IUserService.listByIds(uidList);
        users.forEach(user -> {
            WSBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberAddWS(roomGroup.getRoomId(), user);
            pushService.sendPushMsg(ws, memberUidList);
        });
        //移除缓存
        groupMemberCache.evictMemberUidList(roomGroup.getRoomId());
    }

}
