package com.bin.user.provide;

import com.bin.user.domain.entity.User;
import com.bin.user.domain.enums.ChatActiveStatusEnum;
import com.bin.user.domain.vo.request.common.CursorPageBaseReq;
import com.bin.user.domain.vo.response.common.CursorPageBaseResp;

import java.util.List;
import java.util.Map;

/**
 * @author: bin.jiang
 * @date: 2024/6/22 14:23
 **/

public interface IUserService {
    public CursorPageBaseResp<User> getCursorPage(List<Long> memberUidList, CursorPageBaseReq request, ChatActiveStatusEnum online);
    public List<User> getMemberList();

    public List<User> listByIds(List<Long> list);

    public Map<Long, User> getBatch(List<Long> req);

    public User get(Long userId);

    public Integer getOnlineCount(List<Long> memberUidList);
}
