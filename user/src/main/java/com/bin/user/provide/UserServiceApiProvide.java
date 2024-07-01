package com.bin.user.provide;

import com.bin.api.user.UserServiceApi;
import com.bin.model.common.exception.RoleEnum;
import com.bin.model.user.entity.Black;
import com.bin.model.user.vo.request.user.LoginReqDto;
import com.bin.model.user.vo.response.user.LoginResp;
import com.bin.user.cache.imp.UserInfoCache;
import com.bin.user.dao.BlackDao;
import com.bin.user.dao.UserDao;
import com.bin.model.user.entity.User;
import com.bin.model.common.exception.ChatActiveStatusEnum;
import com.bin.model.user.dto.CursorPageBaseReq;
import com.bin.model.common.vo.response.CursorPageBaseResp;
import com.bin.user.service.RoleService;
import com.bin.user.service.UserService;
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
public class UserServiceApiProvide implements UserServiceApi {

    @Autowired
    UserDao userDao;

    @Autowired
    UserInfoCache userInfoCache;

    @Autowired
    private BlackDao blackDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Override
    public LoginResp getUserId(String token) {
        return userService.getUserIdByToken(token);
    }

    @Override
    public LoginResp login(LoginReqDto dto) {
        return userService.login(dto);
    }

    @Override
    public List<Black> list() {
        return blackDao.list();
    }

    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        return roleService.hasPower(uid,roleEnum);
    }

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
