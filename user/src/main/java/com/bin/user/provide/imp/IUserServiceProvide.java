package com.bin.user.provide.imp;

import com.bin.user.cache.imp.UserInfoCache;
import com.bin.user.dao.UserDao;
import com.bin.user.domain.entity.User;
import com.bin.user.domain.enums.ChatActiveStatusEnum;
import com.bin.user.domain.vo.request.common.CursorPageBaseReq;
import com.bin.user.domain.vo.response.common.CursorPageBaseResp;
import com.bin.user.provide.IUserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author: bin.jiang
 * @date: 2024/6/22 15:08
 **/
@Component
@DubboService
public class IUserServiceProvide implements IUserService {

    @Autowired
    UserDao userDao;

    @Autowired
    UserInfoCache userInfoCache;

    @Override
    public CursorPageBaseResp<User> getCursorPage(List<Long> memberUidList, CursorPageBaseReq request, ChatActiveStatusEnum online) {
        return userDao.getCursorPage(memberUidList,request,online);
    }

    @Override
    public List<User> getMemberList() {
        return userDao.getMemberList();
    }

    @Override
    public List<User> listByIds(List<Long> list) {
        return userDao.listByIds(list);
    }

    @Override
    public Map<Long, User> getBatch(List<Long> req) {
        return userInfoCache.getBatch(req);
    }

    @Override
    public User get(Long userId) {
        return userInfoCache.get(userId);
    }

    @Override
    public Integer getOnlineCount(List<Long> memberUidList) {
        return userDao.getOnlineCount();
    }
}
