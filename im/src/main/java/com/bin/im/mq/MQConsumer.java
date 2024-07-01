package com.bin.im.mq;

import com.bin.im.dao.ContactDao;
import com.bin.im.dao.MessageDao;
import com.bin.im.dao.RoomDao;
import com.bin.im.dao.RoomFriendDao;
import com.bin.im.service.ChatService;
import com.bin.im.service.cache.imp.GroupMemberCache;
import com.bin.im.service.cache.imp.HotRoomCache;
import com.bin.im.service.cache.imp.RoomCache;
import com.bin.api.access.dto.WSAdapter;
import com.bin.model.common.MQConstant;
import com.bin.model.user.dto.MsgSendMessageDTO;
import com.bin.model.im.entity.Message;
import com.bin.model.im.entity.Room;
import com.bin.model.im.entity.RoomFriend;
import com.bin.model.im.vo.response.ChatMessageResp;
import com.bin.model.common.exception.RoomTypeEnum;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Description: 发送消息更新房间收信箱，并同步给房间成员信箱
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-12
 */
@RocketMQMessageListener(consumerGroup = MQConstant.SEND_MSG_GROUP, topic = MQConstant.SEND_MSG_TOPIC)
@Component
public class MQConsumer implements RocketMQListener<MsgSendMessageDTO> {
    @Autowired
    private ChatService chatService;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private RoomCache roomCache;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private GroupMemberCache groupMemberCache;
    @Autowired
    private RoomFriendDao roomFriendDao;
    @Autowired
    private ContactDao contactDao;
    @Autowired
    private HotRoomCache hotRoomCache;
    @Autowired
    private PushService pushService;

    @Override
    public void onMessage(MsgSendMessageDTO dto) {
        Message message = messageDao.getById(dto.getMsgId());
        Room room = roomCache.get(message.getRoomId());
        ChatMessageResp msgResp = chatService.getMsgResp(message, null);
        //所有房间更新房间最新消息
        roomDao.refreshActiveTime(room.getId(), message.getId(), message.getCreateTime());
        roomCache.delete(room.getId());
        if (room.isHotRoom()) {//热门群聊推送所有在线的人
            //更新热门群聊时间-redis
            hotRoomCache.refreshActiveTime(room.getId(), message.getCreateTime());
            //推送所有人
            pushService.sendPushRoomMsg(WSAdapter.buildMsgSend(msgResp), room.getId());
        } else {
            List<Long> memberUidList = new ArrayList<>();
            if (Objects.equals(room.getType(), RoomTypeEnum.GROUP.getType())) {
                memberUidList = groupMemberCache.getMemberUidList(room.getId());
                pushService.sendPushRoomMsg(WSAdapter.buildMsgSend(msgResp), room.getId());
            } else if (Objects.equals(room.getType(), RoomTypeEnum.FRIEND.getType())) {
                //对单人推送
                RoomFriend roomFriend = roomFriendDao.getByRoomId(room.getId());
                memberUidList = Arrays.asList(roomFriend.getUid1(), roomFriend.getUid2());
                pushService.sendPushMsg(WSAdapter.buildMsgSend(msgResp), memberUidList);
            }
            //更新所有群成员的会话时间
            contactDao.refreshOrCreateActiveTime(room.getId(), memberUidList, message.getId(), message.getCreateTime());
        }
    }


}
