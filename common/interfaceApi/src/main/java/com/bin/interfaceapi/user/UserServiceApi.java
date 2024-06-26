package com.bin.interfaceapi.user;

import com.bin.model.user.entity.Black;
import com.bin.model.user.entity.User;

import java.util.List;
import java.util.Map;

/**
 * @author: bin.jiang
 * @date: 2024/6/25 16:40
 **/

public interface UserServiceApi {

    public Long getUserId(String token);

    public List<Black> list();

    boolean hasPower(Long uid, RoleEnum roleEnum);

    public CursorPageBaseResp<User> getCursorPage(List<Long> memberUidList, CursorPageBaseReq request, ChatActiveStatusEnum online);
    public List<User> getMemberList();

    public List<User> listByIds(List<Long> list);

    public Map<Long, User> getBatch(List<Long> req);

    public User get(Long userId);

    public Integer getOnlineCount(List<Long> memberUidList);

}
