package com.davies.naraka.system.service.impl;

import com.davies.naraka.autoconfigure.annotation.DistributedLock;
import com.davies.naraka.autoconfigure.jpa.QuerySQLParams;
import com.davies.naraka.autoconfigure.jpa.SQLExecuteHelper;
import com.davies.naraka.autoconfigure.jpa.SQLParams;
import com.davies.naraka.autoconfigure.jpa.SQLParamsProvider;
import com.davies.naraka.cloud.common.exception.NarakaException;
import com.davies.naraka.cloud.common.exception.ObjectAlreadyExistsException;
import com.davies.naraka.system.domain.entity.SysUser;
import com.davies.naraka.system.repository.SysUserRepository;
import com.davies.naraka.system.service.RoleService;
import com.davies.naraka.system.service.TenementService;
import com.davies.naraka.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author davies
 * @date 2022/5/29 21:32
 */
@Service
public class UserServiceImpl implements UserService {


    private final SysUserRepository userRepository;

    @Autowired
    private RoleService roleService;
    @Autowired
    private TenementService tenementService;
    @Autowired
    private SQLExecuteHelper executeHelper;


    public UserServiceImpl(SysUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @DistributedLock(key = "'user:create:'+#user.username")
    public SysUser createUser(SysUser user, List<String> roles, List<String> tenements) {
        String username = user.getUsername();
        userRepository.findByUsername(username).ifPresent(sysUser -> {
            throw new ObjectAlreadyExistsException("用户名已存在~");
        });
        userRepository.save(user);
        String userId = user.getId();
        if (roles != null && !roles.isEmpty()) {
            roleService.insertUserRole(userId, roles);
        }
        if (tenements != null && !tenements.isEmpty()) {
            tenementService.insertUserTenement(userId, tenements);
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(SysUser sysUser, List<String> roles, List<String> tenements) {
        String userId = sysUser.getId();
        userRepository.findById(userId).orElseThrow(() -> new NarakaException("用户不存在~"));
        QuerySQLParams querySQLParams = SQLParamsProvider.query(Collections.singletonMap("id", userId));
        SQLParams sqlParams = SQLParamsProvider.update(sysUser);
        executeHelper.update(querySQLParams, sqlParams, SysUser.class);
        if (roles != null) {
            roleService.deleteUserRole(userId);
            roleService.insertUserRole(userId, roles);
        }
        if (tenements != null) {
            tenementService.deleteUserTenement(userId);
            tenementService.insertUserTenement(userId, tenements);
        }

    }
}
