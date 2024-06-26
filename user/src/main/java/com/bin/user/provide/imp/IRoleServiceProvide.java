package com.bin.user.provide.imp;

import com.bin.user.dao.RoleDao;
import com.bin.user.domain.enums.RoleEnum;
import com.bin.user.provide.IRoleService;
import com.bin.user.service.RoleService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: bin.jiang
 * @date: 2024/6/22 16:15
 **/
@Component
@DubboService
public class IRoleServiceProvide implements IRoleService {

    @Autowired
    private RoleService roleService;

    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        return roleService.hasPower(uid,roleEnum);
    }
}
