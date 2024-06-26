package com.bin.interfaceapi.access.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WSRespTypeEnum {
    LOGIN_URL(1, "登录二维码返回"),
    LOGIN_SCAN_SUCCESS(2, "用户扫描成功等待授权"),
    LOGIN_SUCCESS(3, "用户登录成功返回用户信息"),
    MESSAGE(4, "新消息"),
    ONLINE_OFFLINE_NOTIFY(5, "上下线通知"),
    INVALIDATE_TOKEN(6, "使前端的token失效，意味着前端需要重新登录"),
    BLACK(7, "拉黑用户"),
    MARK(8, "消息标记"),
    RECALL(9, "消息撤回"),
    APPLY(10, "好友申请"),
    MEMBER_CHANGE(11, "成员变动"),
    ;

    private final Integer type;
    private final String desc;
}
