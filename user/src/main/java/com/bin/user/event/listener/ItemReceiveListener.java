package com.bin.user.event.listener;

import com.bin.user.cache.imp.ItemCache;
import com.bin.user.cache.imp.UserCache;
import com.bin.user.dao.UserDao;
import com.bin.model.user.entity.ItemConfig;
import com.bin.model.user.entity.User;
import com.bin.model.user.entity.UserBackpack;
import com.bin.model.user.enums.ItemTypeEnum;
import com.bin.user.event.ItemReceiveEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 用户收到物品监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class ItemReceiveListener {
    @Autowired
    private UserDao userDao;
    @Autowired
    private ItemCache itemCache;
    @Autowired
    private UserCache userCache;

    /**
     * 徽章类型，帮忙默认佩戴
     *
     * @param event
     */
    @Async
    @EventListener(classes = ItemReceiveEvent.class)
    public void wear(ItemReceiveEvent event) {
        UserBackpack userBackpack = event.getUserBackpack();
        ItemConfig itemConfig = itemCache.getById(userBackpack.getItemId());
        if (ItemTypeEnum.BADGE.getType().equals(itemConfig.getType())) {
            User user = userDao.getById(userBackpack.getUid());
            if (Objects.isNull(user.getItemId())) {
                userDao.wearingBadge(userBackpack.getUid(), userBackpack.getItemId());
                userCache.userInfoChange(userBackpack.getUid());
            }
        }
    }

}
