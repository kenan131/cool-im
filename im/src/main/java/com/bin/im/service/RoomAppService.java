package com.bin.im.service;

import com.bin.im.domain.vo.request.*;
import com.bin.im.domain.vo.request.common.CursorPageBaseReq;
import com.bin.im.domain.vo.request.member.MemberAddReq;
import com.bin.im.domain.vo.request.member.MemberDelReq;
import com.bin.im.domain.vo.request.member.MemberReq;
import com.bin.im.domain.vo.response.ChatMemberListResp;
import com.bin.im.domain.vo.response.ChatRoomResp;
import com.bin.im.domain.vo.response.MemberResp;
import com.bin.im.domain.vo.response.common.CursorPageBaseResp;
import com.bin.im.domain.ws.ChatMemberResp;

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
