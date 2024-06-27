package com.bin.im.provide;

import com.bin.im.dao.GroupMemberDao;
import com.bin.model.im.entity.RoomFriend;
import com.bin.im.service.ChatService;
import com.bin.im.service.RoomService;
import com.bin.im.service.adapter.MessageAdapter;
import com.bin.api.im.ImServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author: bin.jiang
 * @date: 2024/6/26 14:14
 **/
@Component
public class ImProvideApi implements ImServiceApi {

    @Autowired
    private RoomService roomService;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private ChatService chatService;

    @Override
    public Map<Long, List<Long>> getMemberIdsByRoomIds(List<Long> roomIds) {

        return null;
    }

    @Override
    public boolean createFriendRoom(List<Long> uidList,Long uid) {
        RoomFriend roomFriend = roomService.createFriendRoom(uidList);
        //发送一条同意消息。。我们已经是好友了，开始聊天吧
        chatService.sendMsg(MessageAdapter.buildAgreeMsg(roomFriend.getRoomId()), uid);
        return true;
    }

    @Override
    public void disableFriendRoom(List<Long> uidList) {
        roomService.disableFriendRoom(uidList);
    }
}
