package com.bin.im.service.cache.imp;

import cn.hutool.core.lang.Pair;
import com.bin.im.domain.vo.request.common.CursorPageBaseReq;
import com.bin.im.domain.vo.response.common.CursorPageBaseResp;
import com.bin.im.util.CursorUtils;
import com.bin.im.util.RedisKey;
import com.bin.im.util.RedisUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

/**
 * Description: 全局房间
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-07-23
 */
@Component
public class HotRoomCache {

    /**
     * 获取热门群聊翻页
     *
     * @return
     */
    public CursorPageBaseResp<Pair<Long, Double>> getRoomCursorPage(CursorPageBaseReq pageBaseReq) {
        return CursorUtils.getCursorPageByRedis(pageBaseReq, RedisKey.getKey(RedisKey.HOT_ROOM_ZET), Long::parseLong);
    }

    public Set<ZSetOperations.TypedTuple<String>> getRoomRange(Double hotStart, Double hotEnd) {
        return RedisUtils.zRangeByScoreWithScores(RedisKey.getKey(RedisKey.HOT_ROOM_ZET), hotStart, hotEnd);
    }

    /**
     * 更新热门群聊的最新时间
     */
    public void refreshActiveTime(Long roomId, Date refreshTime) {
        RedisUtils.zAdd(RedisKey.getKey(RedisKey.HOT_ROOM_ZET), roomId, (double) refreshTime.getTime());
    }
}
