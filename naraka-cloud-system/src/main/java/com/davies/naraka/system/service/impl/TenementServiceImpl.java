package com.davies.naraka.system.service.impl;

import com.davies.naraka.autoconfigure.jpa.QuerySQLParams;
import com.davies.naraka.autoconfigure.jpa.SQLExecuteHelper;
import com.davies.naraka.autoconfigure.jpa.SQLParams;
import com.davies.naraka.autoconfigure.jpa.SQLParamsProvider;
import com.davies.naraka.cloud.common.exception.NarakaException;
import com.davies.naraka.system.domain.entity.SysTenement;
import com.davies.naraka.system.domain.entity.SysUser;
import com.davies.naraka.system.domain.entity.SysUserTenement;
import com.davies.naraka.system.domain.entity.SysUserTenementId;
import com.davies.naraka.system.repository.SysTenementRepository;
import com.davies.naraka.system.repository.SysUserTenementRepository;
import com.davies.naraka.system.service.RoleService;
import com.davies.naraka.system.service.TenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author davies
 * @date 2022/6/2 11:00
 */
@Service
public class TenementServiceImpl implements TenementService {


    @Autowired
    private SysTenementRepository tenementRepository;
    @Autowired
    private SysUserTenementRepository userTenementRepository;
    private final SQLExecuteHelper executeHelper;


    @Autowired
    private RoleService roleService;

    public TenementServiceImpl(SQLExecuteHelper executeHelper) {
        this.executeHelper = executeHelper;
    }

    @Override
    public SysTenement createTenement(SysTenement tenement) {
        tenementRepository.save(tenement);
        return tenement;
    }

    @Override
    public void deleteUserTenement(String userId) {
        userTenementRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserTenement(String userId, List<String> tenementId) {
        for (String id : tenementId) {
            if (!tenementRepository.existsById(id)) {
                throw new NarakaException("租户不存在~");
            }
            SysUserTenement userRole = new SysUserTenement();
            userRole.setId(new SysUserTenementId(userId, id));
            userTenementRepository.save(userRole);
        }
    }

    @Override
    public void updateTenement(SysTenement tenement, List<String> roles) {
        String id = tenement.getCode();
        tenementRepository.findById(id).orElseThrow(() -> new NarakaException("租户不存在~"));
        QuerySQLParams querySQLParams = SQLParamsProvider.query(Collections.singletonMap("code", id));
        SQLParams sqlParams = SQLParamsProvider.update(tenement);
        executeHelper.update(querySQLParams, sqlParams, SysUser.class);
        if (roles != null) {
            roleService.deleteTenementRole(id);
            roleService.insertTenementRole(id, roles);
        }

    }
}
