package com.bin.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bin.user.domain.entity.ItemConfig;
import com.bin.user.mapper.ItemConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig> {

    public List<ItemConfig> getByType(Integer type) {
        return lambdaQuery().eq(ItemConfig::getType, type).list();
    }
}
