package com.bin.im.service;

import com.bin.model.im.vo.request.*;
import com.bin.model.user.dto.CursorPageBaseReq;
import com.bin.model.im.vo.request.ChatMessageMemberReq;
import com.bin.model.im.vo.request.member.MemberAddReq;
import com.bin.model.im.vo.request.member.MemberDelReq;
import com.bin.model.im.vo.request.member.MemberReq;
import com.bin.model.im.vo.response.ChatMemberListResp;
import com.bin.model.im.vo.response.ChatRoomResp;
import com.bin.model.im.vo.response.MemberResp;
import com.bin.model.common.vo.response.CursorPageBaseResp;
import com.bin.model.user.vo.response.ws.ChatMemberResp;

import java.util.List;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-07-22
 */
public interface RoomAppService {
    /**
     * 获取会话列表--支持未登录态
     */
    CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid);

    /**
     * 获取群组信息
     */
    MemberResp getGroupDetail(Long uid, long roomId);

    CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq request);

    List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request);

    void delMember(Long uid, MemberDelReq request);

    void addMember(Long uid, MemberAddReq request);

    Long addGroup(Long uid, GroupAddReq request);

    ChatRoomResp getContactDetail(Long uid, Long roomId);

    ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid);
}
