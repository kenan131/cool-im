package com.bin.im.provide;

import com.bin.im.dao.GroupMemberDao;
import com.bin.im.service.cache.imp.GroupMemberCache;
import com.bin.im.service.cache.imp.RoomCache;
import com.bin.im.service.cache.imp.RoomGroupCache;
import com.bin.model.im.entity.Room;
import com.bin.model.im.entity.RoomFriend;
import com.bin.im.service.ChatService;
import com.bin.im.service.RoomService;
import com.bin.im.service.adapter.MessageAdapter;
import com.bin.api.im.ImServiceApi;
import com.bin.model.im.entity.RoomGroup;
import com.bin.model.common.exception.RoomTypeEnum;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: bin.jiang
 * @date: 2024/6/26 14:14
 **/
@Component
@DubboService
public class ImProvideApi implements ImServiceApi {

    @Autowired
    private RoomService roomService;

    @Autowired
    private GroupMemberCache groupMemberCache;

    @Autowired
    private ChatService chatService;

    @Override
    public Map<Long, List<Long>> getMemberIdsByRoomIds(List<Long> roomIds) {
        HashMap<Long, List<Long>> map = new HashMap<>();
        for(Long roomId : roomIds){
            List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
            map.put(roomId,memberUidList);
        }
        return map;
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
