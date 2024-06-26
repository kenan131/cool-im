package com.bin.im.event;

import com.bin.im.domain.entity.GroupMember;
import com.bin.im.domain.entity.RoomGroup;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class GroupMemberAddEvent extends ApplicationEvent {

    private final List<GroupMember> memberList;
    private final RoomGroup roomGroup;
    private final Long inviteUid;

    public GroupMemberAddEvent(Object source, RoomGroup roomGroup, List<GroupMember> memberList, Long inviteUid) {
        super(source);
        this.memberList = memberList;
        this.roomGroup = roomGroup;
        this.inviteUid = inviteUid;
    }

}
