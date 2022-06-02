package com.davies.naraka.system.service.impl;

import com.davies.naraka.cloud.common.exception.NarakaException;
import com.davies.naraka.system.domain.entity.SysRole;
import com.davies.naraka.system.domain.entity.SysUserRole;
import com.davies.naraka.system.domain.entity.SysUserRoleId;
import com.davies.naraka.system.repository.SysRoleRepository;
import com.davies.naraka.system.repository.SysUserRoleRepository;
import com.davies.naraka.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author davies
 * @date 2022/6/1 17:24
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private SysRoleRepository roleRepository;

    @Autowired
    private SysUserRoleRepository userRoleRepository;


    @Override
    public SysRole createRole(SysRole role) {
        roleRepository.save(role);
        return role;
    }

    @Override
    public void deleteUserRole(String userId) {
        userRoleRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserRole(String userId, List<String> roles) {
        for (String id : roles) {
            if (!roleRepository.existsById(id)) {
                throw new NarakaException("角色不存在~");
            }
            SysUserRole userRole = new SysUserRole();
            userRole.setId(new SysUserRoleId(userId, id));
            userRoleRepository.save(userRole);
        }
    }
}
