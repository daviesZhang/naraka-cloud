package com.davies.naraka.system.service.impl;

import com.davies.naraka.autoconfigure.jpa.QuerySQLParams;
import com.davies.naraka.autoconfigure.jpa.SQLExecuteHelper;
import com.davies.naraka.autoconfigure.jpa.SQLParams;
import com.davies.naraka.autoconfigure.jpa.SQLParamsProvider;
import com.davies.naraka.system.domain.entity.SysAuthority;
import com.davies.naraka.system.repository.SysAuthorityRepository;
import com.davies.naraka.system.repository.SysRoleAuthorityRepository;
import com.davies.naraka.system.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * @author davies
 * @date 2022/6/5 21:13
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    SysRoleAuthorityRepository roleAuthorityRepository;


    @Autowired
    SysAuthorityRepository authorityRepository;

    @Autowired
    private SQLExecuteHelper executeHelper;

    @Override
    public SysAuthority createAuthority(SysAuthority authority) {
        authorityRepository.save(authority);
        return authority;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAuthority(String id) {
        if (authorityRepository.findById(id).isPresent()) {
            authorityRepository.deleteById(id);
            roleAuthorityRepository.deleteByAuthorityId(id);
        }


    }

    @Override
    public void updateAuthority(SysAuthority authority) {
        String id = authority.getId();
        QuerySQLParams querySQLParams = SQLParamsProvider.query(Collections.singletonMap("id", id));
        SQLParams sqlParams = SQLParamsProvider.update(authority);
        executeHelper.update(querySQLParams, sqlParams, SysAuthority.class);

    }
}
