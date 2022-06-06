package com.davies.naraka.system.service.impl;

import com.davies.naraka.cloud.common.exception.NarakaException;
import com.davies.naraka.system.domain.entity.*;
import com.davies.naraka.system.repository.SysRoleRepository;
import com.davies.naraka.system.repository.SysTenementRoleRepository;
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

    @Autowired
    private SysTenementRoleRepository tenementRoleRepository;

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
    public void deleteRole(String code) {
        if (roleRepository.findById(code).isPresent()) {
            roleRepository.deleteById(code);
            userRoleRepository.deleteByRoleCode(code);
            tenementRoleRepository.deleteByRoleCode(code);
        }
    }

    @Override
    public void deleteTenementRole(String tenementId) {
        tenementRoleRepository.deleteByTenementId(tenementId);
    }

    @Override
    public void insertTenementRole(String tenementId, List<String> roles) {
        for (String id : roles) {
            if (!roleRepository.existsById(id)) {
                throw new NarakaException("角色不存在~");
            }
            SysTenementRole tenementRole = new SysTenementRole();
            tenementRole.setId(new SysTenementRoleId(tenementId, id));
            tenementRoleRepository.save(tenementRole);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetTenementRole(String tenementId, List<String> roles) {
        this.deleteTenementRole(tenementId);
        this.insertTenementRole(tenementId, roles);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetUserRole(String userId, List<String> roles) {
        this.deleteUserRole(userId);
        this.insertUserRole(userId, roles);
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
