package com.davies.naraka.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.davies.naraka.admin.mapper.RoleMapper;
import com.davies.naraka.admin.domain.entity.Role;
import com.davies.naraka.admin.domain.entity.UserRole;
import com.davies.naraka.admin.service.IRoleService;
import com.davies.naraka.admin.service.IUserRoleService;
import com.davies.naraka.admin.service.exception.RoleExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {


    @Autowired
    private IUserRoleService userRoleService;

    @Override
    public Role createRole(Role role) {
        Role checkExist = getOne(new LambdaQueryWrapper<Role>().eq(Role::getCode, role.getCode()));
        if (checkExist!=null){
            throw new RoleExistsException(String.format("%s exist", role.getCode()));
        }
        save(role);
        return role;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void delete(String code) {
        userRoleService.remove(new LambdaQueryWrapper<UserRole>().eq(UserRole::getCode, code));
        baseMapper.delete(new LambdaQueryWrapper<Role>().eq(Role::getCode, code));

    }
}
