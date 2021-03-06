package com.davies.naraka.system.service.impl;

import com.davies.naraka.autoconfigure.jpa.QuerySQLParams;
import com.davies.naraka.autoconfigure.jpa.SQLExecuteHelper;
import com.davies.naraka.autoconfigure.jpa.SQLParams;
import com.davies.naraka.autoconfigure.jpa.SQLParamsProvider;
import com.davies.naraka.cloud.common.exception.NarakaException;
import com.davies.naraka.system.domain.entity.*;
import com.davies.naraka.system.repository.SysRoleAuthorityRepository;
import com.davies.naraka.system.repository.SysRoleRepository;
import com.davies.naraka.system.repository.SysTenementRoleRepository;
import com.davies.naraka.system.repository.SysUserRoleRepository;
import com.davies.naraka.system.service.AuthorityService;
import com.davies.naraka.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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


    @Autowired
    private SQLExecuteHelper executeHelper;

    @Autowired
    private SysRoleAuthorityRepository roleAuthorityRepository;

    @Autowired
    private AuthorityService authorityService;

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
    public void deleteRole(List<String> codes) {
        for (String code : codes) {
            if (roleRepository.findById(code).isPresent()) {
                roleRepository.deleteById(code);
                userRoleRepository.deleteByRoleCode(code);
                tenementRoleRepository.deleteByRoleCode(code);
            }
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
                throw new NarakaException("???????????????~");
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
                throw new NarakaException("???????????????~");
            }
            SysUserRole userRole = new SysUserRole();
            userRole.setId(new SysUserRoleId(userId, id));
            userRoleRepository.save(userRole);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(SysRole role, List<String> authorities) {
        String code = role.getCode();
        roleRepository.findById(code).orElseThrow(() -> new NarakaException("???????????????~"));
        QuerySQLParams querySQLParams = SQLParamsProvider.query(Collections.singletonMap("code", code));
        SQLParams sqlParams = SQLParamsProvider.update(role);
        executeHelper.update(querySQLParams, sqlParams, SysUser.class);
        if (authorities != null) {
            authorityService.resetRoleAuthority(code, authorities);
        }

    }
}
