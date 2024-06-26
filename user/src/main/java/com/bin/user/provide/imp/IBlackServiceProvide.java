package com.bin.user.provide.imp;

import com.bin.user.dao.BlackDao;
import com.bin.user.domain.entity.Black;
import com.bin.user.provide.IBlackService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: bin.jiang
 * @date: 2024/6/22 16:14
 **/
@Component
@DubboService
public class IBlackServiceProvide implements IBlackService {

    @Autowired
    private BlackDao blackDao;

    @Override
    public List<Black> list() {
        return blackDao.list();
    }
}
