package com.bin.user.controller;

import com.bin.user.domain.vo.request.common.CursorPageBaseReq;
import com.bin.user.domain.vo.request.common.PageBaseReq;
import com.bin.user.domain.vo.request.friend.FriendApplyReq;
import com.bin.user.domain.vo.request.friend.FriendApproveReq;
import com.bin.user.domain.vo.request.friend.FriendCheckReq;
import com.bin.user.domain.vo.request.friend.FriendDeleteReq;
import com.bin.user.domain.vo.response.common.ApiResult;
import com.bin.user.domain.vo.response.common.CursorPageBaseResp;
import com.bin.user.domain.vo.response.common.PageBaseResp;
import com.bin.user.domain.vo.response.friend.FriendApplyResp;
import com.bin.user.domain.vo.response.friend.FriendCheckResp;
import com.bin.user.domain.vo.response.friend.FriendResp;
import com.bin.user.domain.vo.response.friend.FriendUnreadResp;
import com.bin.user.service.FriendService;
import com.bin.user.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 好友相关接口
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-07-16
 */
@RestController
@RequestMapping("/capi/user/friend")
@Api(tags = "好友相关接口")
@Slf4j
public class FriendController {
    @Resource
    private FriendService friendService;

    @GetMapping("/check")
    @ApiOperation("批量判断是否是自己好友")
    public ApiResult<FriendCheckResp> check(@Valid FriendCheckReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.check(uid, request));
    }

    @PostMapping("/apply")
    @ApiOperation("申请好友")
    public ApiResult<Void> apply(@Valid @RequestBody FriendApplyReq request) {
        Long uid = RequestHolder.get().getUid();
        friendService.apply(uid, request);
        return ApiResult.success();
    }

    @DeleteMapping()
    @ApiOperation("删除好友")
    public ApiResult<Void> delete(@Valid @RequestBody FriendDeleteReq request) {
        Long uid = RequestHolder.get().getUid();
        friendService.deleteFriend(uid, request.getTargetUid());
        return ApiResult.success();
    }

    @GetMapping("/apply/page")
    @ApiOperation("好友申请列表")
    public ApiResult<PageBaseResp<FriendApplyResp>> page(@Valid PageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.pageApplyFriend(uid, request));
    }

    @GetMapping("/apply/unread")
    @ApiOperation("申请未读数")
    public ApiResult<FriendUnreadResp> unread() {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.unread(uid));
    }

    @PutMapping("/apply")
    @ApiOperation("审批同意")
    public ApiResult<Void> applyApprove(@Valid @RequestBody FriendApproveReq request) {
        friendService.applyApprove(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }

    @GetMapping("/page")
    @ApiOperation("联系人列表")
    public ApiResult<CursorPageBaseResp<FriendResp>> friendList(@Valid CursorPageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.friendList(uid, request));
    }
}

