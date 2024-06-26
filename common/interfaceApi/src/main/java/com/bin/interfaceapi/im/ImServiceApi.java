package com.bin.interfaceapi.im;

import com.bin.model.im.RoomFriend;

import java.util.List;
import java.util.Map;

/**
 * @author: bin.jiang
 * @date: 2024/6/25 14:37
 **/

public interface ImServiceApi {

    Map<Long, List<Long>> getMemberIdsByRoomIds(List<Long> roomIds);

    boolean createFriendRoom(List<Long> uidList,Long uid);

    void disableFriendRoom(List<Long> uidList);

}
