package com.bin.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bin.interfaceapi.im.ImServiceApi;
import com.bin.user.aspect.RedissonLock;
import com.bin.user.dao.UserApplyDao;
import com.bin.user.dao.UserDao;
import com.bin.user.dao.UserFriendDao;
import com.bin.user.domain.entity.User;
import com.bin.user.domain.entity.UserApply;
import com.bin.user.domain.entity.UserFriend;
import com.bin.user.domain.vo.request.common.CursorPageBaseReq;
import com.bin.user.domain.vo.request.common.PageBaseReq;
import com.bin.user.domain.vo.request.friend.FriendApplyReq;
import com.bin.user.domain.vo.request.friend.FriendApproveReq;
import com.bin.user.domain.vo.request.friend.FriendCheckReq;
import com.bin.user.domain.vo.response.common.CursorPageBaseResp;
import com.bin.user.domain.vo.response.common.PageBaseResp;
import com.bin.user.domain.vo.response.friend.FriendApplyResp;
import com.bin.user.domain.vo.response.friend.FriendCheckResp;
import com.bin.user.domain.vo.response.friend.FriendResp;
import com.bin.user.domain.vo.response.friend.FriendUnreadResp;
import com.bin.user.event.UserApplyEvent;
import com.bin.user.service.FriendService;
import com.bin.user.utils.AssertUtil;
import com.bin.user.utils.adapter.FriendAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.bin.user.domain.enums.ApplyStatusEnum.WAIT_APPROVAL;

@Slf4j
@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private UserFriendDao userFriendDao;
    @Autowired
    private UserApplyDao userApplyDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @DubboReference(check = false)
    private ImServiceApi imServiceApi;

    @Autowired
    private UserDao userDao;

    /**
     * 检查
     * 检查是否是自己好友
     *
     * @param uid     uid
     * @param request 请求
     * @return {@link FriendCheckResp}
     */
    @Override
    public FriendCheckResp check(Long uid, FriendCheckReq request) {
        List<UserFriend> friendList = userFriendDao.getByFriends(uid, request.getUidList());

        Set<Long> friendUidSet = friendList.stream().map(UserFriend::getFriendUid).collect(Collectors.toSet());
        List<FriendCheckResp.FriendCheck> friendCheckList = request.getUidList().stream().map(friendUid -> {
            FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
            friendCheck.setUid(friendUid);
            friendCheck.setIsFriend(friendUidSet.contains(friendUid));
            return friendCheck;
        }).collect(Collectors.toList());
        return new FriendCheckResp(friendCheckList);
    }

    /**
     * 申请好友
     *
     * @param request 请求
     */
    @Override
    @RedissonLock(key = "#uid")
    public void apply(Long uid, FriendApplyReq request) {
        //是否有好友关系
        UserFriend friend = userFriendDao.getByFriend(uid, request.getTargetUid());
        AssertUtil.isEmpty(friend, "你们已经是好友了");
        //是否有待审批的申请记录(自己的)
        UserApply selfApproving = userApplyDao.getFriendApproving(uid, request.getTargetUid());
        if (Objects.nonNull(selfApproving)) {
            log.info("已有好友申请记录,uid:{}, targetId:{}", uid, request.getTargetUid());
            return;
        }
        //是否有待审批的申请记录(别人请求自己的)
        UserApply friendApproving = userApplyDao.getFriendApproving(request.getTargetUid(), uid);
        if (Objects.nonNull(friendApproving)) {
            ((FriendService) AopContext.currentProxy()).applyApprove(uid, new FriendApproveReq(friendApproving.getId()));
            return;
        }
        //申请入库
        UserApply insert = FriendAdapter.buildFriendApply(uid, request);
        userApplyDao.save(insert);
        //申请事件
        applicationEventPublisher.publishEvent(new UserApplyEvent(this, insert));
    }

    /**
     * 分页查询好友申请
     *
     * @param request 请求
     * @return {@link PageBaseResp}<{@link FriendApplyResp}>
     */
    @Override
    public PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request) {
        IPage<UserApply> userApplyIPage = userApplyDao.friendApplyPage(uid, request.plusPage());
        if (CollectionUtil.isEmpty(userApplyIPage.getRecords())) {
            return PageBaseResp.empty();
        }
        //将这些申请列表设为已读
        readApples(uid, userApplyIPage);
        //返回消息
        return PageBaseResp.init(userApplyIPage, FriendAdapter.buildFriendApplyList(userApplyIPage.getRecords()));
    }

    private void readApples(Long uid, IPage<UserApply> userApplyIPage) {
        List<Long> applyIds = userApplyIPage.getRecords()
                .stream().map(UserApply::getId)
                .collect(Collectors.toList());
        userApplyDao.readApples(uid, applyIds);
    }

    /**
     * 申请未读数
     *
     * @return {@link FriendUnreadResp}
     */
    @Override
    public FriendUnreadResp unread(Long uid) {
        Integer unReadCount = userApplyDao.getUnReadCount(uid);
        return new FriendUnreadResp(unReadCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid")
    public void applyApprove(Long uid, FriendApproveReq request) {
        UserApply userApply = userApplyDao.getById(request.getApplyId());
        AssertUtil.isNotEmpty(userApply, "不存在申请记录");
        AssertUtil.equal(userApply.getTargetId(), uid, "不存在申请记录");
        AssertUtil.equal(userApply.getStatus(), WAIT_APPROVAL.getCode(), "已同意好友申请");
        //同意申请
        userApplyDao.agree(request.getApplyId());
        //创建双方好友关系
        createFriend(uid, userApply.getUid());
        // 调用im服务创建房间号 并发送好友同意申请通知。   TODO 事务相关
        imServiceApi.createFriendRoom(Arrays.asList(uid, userApply.getUid()),uid);
    }

    /**
     * 删除好友
     *
     * @param uid       uid
     * @param friendUid 朋友uid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(Long uid, Long friendUid) {
        List<UserFriend> userFriends = userFriendDao.getUserFriend(uid, friendUid);
        if (CollectionUtil.isEmpty(userFriends)) {
            log.info("没有好友关系：{},{}", uid, friendUid);
            return;
        }
        List<Long> friendRecordIds = userFriends.stream().map(UserFriend::getId).collect(Collectors.toList());
        userFriendDao.removeByIds(friendRecordIds);
        //禁用房间  TODO  dubbo调用事务相关问题。
        imServiceApi.disableFriendRoom(Arrays.asList(uid, friendUid));
    }

    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {
        CursorPageBaseResp<UserFriend> friendPage = userFriendDao.getFriendPage(uid, request);
        if (CollectionUtils.isEmpty(friendPage.getList())) {
            return CursorPageBaseResp.empty();
        }
        List<Long> friendUids = friendPage.getList()
                .stream().map(UserFriend::getFriendUid)
                .collect(Collectors.toList());
        List<User> userList = userDao.getFriendList(friendUids);
        return CursorPageBaseResp.init(friendPage, FriendAdapter.buildFriend(friendPage.getList(), userList));
    }

    private void createFriend(Long uid, Long targetUid) {
        UserFriend userFriend1 = new UserFriend();
        userFriend1.setUid(uid);
        userFriend1.setFriendUid(targetUid);
        UserFriend userFriend2 = new UserFriend();
        userFriend2.setUid(targetUid);
        userFriend2.setFriendUid(uid);
        ArrayList<UserFriend> list = new ArrayList(2);
        Collections.addAll(list, userFriend1,userFriend2);
        userFriendDao.saveBatch(list);
    }

}
