package com.bin.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.bin.api.access.dto.WSAdapter;
import com.bin.model.common.exception.RoleEnum;
import com.bin.model.user.vo.response.user.LoginResp;
import com.bin.user.cache.imp.ItemCache;
import com.bin.user.cache.imp.UserCache;
import com.bin.user.cache.imp.UserSummaryCache;
import com.bin.user.dao.BlackDao;
import com.bin.user.dao.ItemConfigDao;
import com.bin.user.dao.UserBackpackDao;
import com.bin.user.dao.UserDao;
import com.bin.model.user.dto.ItemInfoDTO;
import com.bin.model.user.dto.SummeryInfoDTO;
import com.bin.model.user.entity.Black;
import com.bin.model.user.entity.ItemConfig;
import com.bin.model.user.entity.User;
import com.bin.model.user.entity.UserBackpack;
import com.bin.model.common.exception.BlackTypeEnum;
import com.bin.model.common.exception.ItemEnum;
import com.bin.model.common.exception.ItemTypeEnum;
import com.bin.model.user.vo.request.user.*;
import com.bin.model.user.vo.response.user.BadgeResp;
import com.bin.model.user.vo.response.user.UserInfoResp;
import com.bin.user.event.UserBlackEvent;
import com.bin.user.service.LoginService;
import com.bin.user.service.RoleService;
import com.bin.user.service.UserService;
import com.bin.user.utils.AssertUtil;
import com.bin.user.utils.adapter.UserAdapter;
import com.bin.user.utils.sensitive.SensitiveWordBs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Description: 用户基础操作类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserCache userCache;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ItemConfigDao itemConfigDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ItemCache itemCache;
    @Autowired
    private BlackDao blackDao;
    @Autowired
    private UserSummaryCache userSummaryCache;
    @Autowired
    private SensitiveWordBs sensitiveWordBs;
    @Autowired
    private LoginService LoginServiceImpl;
    @Autowired
    private RoleService roleService;

    @Override
    public LoginResp login(LoginReqDto dto) {
        User user = userDao.getUserByName(dto.getUserName());
        if(user == null){
            return LoginResp.buildFailResp("账号有误！");
        }
        if(!user.getPassword().equals(dto.getPassWord())){
            return LoginResp.buildFailResp("密码有误！");
        }
        String token = LoginServiceImpl.login(user.getId());
        boolean hasPower = roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER);
        return LoginResp.buildSuccessResp(user.getId(),WSAdapter.buildLoginSuccessResp(user,token,hasPower));
    }
    @Override
    public LoginResp getUserIdByToken(String token){
        Long uid = LoginServiceImpl.getValidUid(token);
        if(uid != null){
            User user = userCache.getUserInfo(uid);
            boolean hasPower = roleService.hasPower(uid, RoleEnum.CHAT_MANAGER);
            return LoginResp.buildSuccessResp(uid,WSAdapter.buildLoginSuccessResp(user,token,hasPower));
        }
        return null;
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User userInfo = userCache.getUserInfo(uid);
        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfoResp(userInfo, countByValidItemId);
    }

    @Override
    @Transactional
    public void modifyName(Long uid, ModifyNameReq req) {
        //判断名字是不是重复
        String newName = req.getName();
        AssertUtil.isFalse(sensitiveWordBs.hasSensitiveWord(newName), "名字中包含敏感词，请重新输入"); // 判断名字中有没有敏感词
        User oldUser = userDao.getByName(newName);
        AssertUtil.isEmpty(oldUser, "名字已经被抢占了，请换一个哦~~");
        //判断改名卡够不够
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(firstValidItem, "改名次数不够了，等后续活动送改名卡哦");
        //使用改名卡
        boolean useSuccess = userBackpackDao.invalidItem(firstValidItem.getId());
        if (useSuccess) {//用乐观锁，就不用分布式锁了
            //改名
            userDao.modifyName(uid, req.getName());
            //删除缓存
            userCache.userInfoChange(uid);
        }
    }

    @Override
    public List<BadgeResp> badges(Long uid) {
        //查询所有徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        //查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid, itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        //查询用户当前佩戴的标签
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(itemConfigs, backpacks, user);
    }

    @Override
    public void wearingBadge(Long uid, WearingBadgeReq req) {
        //确保有这个徽章
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, req.getBadgeId());
        AssertUtil.isNotEmpty(firstValidItem, "您没有这个徽章哦，快去达成条件获取吧");
        //确保物品类型是徽章
        ItemConfig itemConfig = itemConfigDao.getById(firstValidItem.getItemId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "该徽章不可佩戴");
        //佩戴徽章
        userDao.wearingBadge(uid, req.getBadgeId());
        //删除用户缓存
        userCache.userInfoChange(uid);
    }

    @Override
    public void register(User user) {
        userDao.save(user);
//        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, user));
    }



    @Override
    public void black(BlackReq req) {
        Long uid = req.getUid();
        Black user = new Black();
        user.setTarget(uid.toString());
        user.setType(BlackTypeEnum.UID.getType());
        blackDao.save(user);
        User byId = userDao.getById(uid);
        blackIp(byId.getIpInfo().getCreateIp());
        blackIp(byId.getIpInfo().getUpdateIp());
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, byId));
    }

    @Override
    public List<SummeryInfoDTO> getSummeryUserInfo(SummeryInfoReq req) {
        //需要前端同步的uid
        List<Long> uidList = getNeedSyncUidList(req.getReqList());
        //加载用户信息
        Map<Long, SummeryInfoDTO> batch = userSummaryCache.getBatch(uidList);
        return req.getReqList()
                .stream()
                .map(a -> batch.containsKey(a.getUid()) ? batch.get(a.getUid()) : SummeryInfoDTO.skip(a.getUid()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemInfoDTO> getItemInfo(ItemInfoReq req) {//简单做，更新时间可判断被修改
        return req.getReqList().stream().map(a -> {
            ItemConfig itemConfig = itemCache.getById(a.getItemId());
            if (Objects.nonNull(a.getLastModifyTime()) && a.getLastModifyTime() >= itemConfig.getUpdateTime().getTime()) {
                return ItemInfoDTO.skip(a.getItemId());
            }
            ItemInfoDTO dto = new ItemInfoDTO();
            dto.setItemId(itemConfig.getId());
            dto.setImg(itemConfig.getImg());
            dto.setDescribe(itemConfig.getDescribe());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<Long> getNeedSyncUidList(List<SummeryInfoReq.infoReq> reqList) {
        List<Long> needSyncUidList = new ArrayList<>();
        List<Long> userModifyTime = userCache.getUserModifyTime(reqList.stream().map(SummeryInfoReq.infoReq::getUid).collect(Collectors.toList()));
        for (int i = 0; i < reqList.size(); i++) {
            SummeryInfoReq.infoReq infoReq = reqList.get(i);
            Long modifyTime = userModifyTime.get(i);
            if (Objects.isNull(infoReq.getLastModifyTime()) || (Objects.nonNull(modifyTime) && modifyTime > infoReq.getLastModifyTime())) {
                needSyncUidList.add(infoReq.getUid());
            }
        }
        return needSyncUidList;
    }

    public void blackIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return;
        }
        try {
            Black user = new Black();
            user.setTarget(ip);
            user.setType(BlackTypeEnum.IP.getType());
            blackDao.save(user);
        } catch (Exception e) {
            log.error("duplicate black ip:{}", ip);
        }
    }
}
