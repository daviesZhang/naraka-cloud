package com.davies.naraka.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.davies.naraka.mapper.UserRoleMapper;
import com.davies.naraka.domain.entity.UserRole;
import com.davies.naraka.service.IUserRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {


    @Override
    public Integer createUserRole(UserRole userRole) {
        saveOrUpdate(userRole, new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUsername, userRole.getUsername()));
        return userRole.getId();
    }


}
