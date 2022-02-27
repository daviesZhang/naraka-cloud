package com.davies.naraka.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.davies.naraka.mapper.AuthorityRoleMapper;
import com.davies.naraka.domain.entity.AuthorityRole;
import com.davies.naraka.service.IAuthorityRoleService;
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
public class AuthorityRoleServiceImpl extends ServiceImpl<AuthorityRoleMapper, AuthorityRole> implements IAuthorityRoleService {


    @Override
    public Integer createAuthorityRole(AuthorityRole authorityRole) {

        saveOrUpdate(authorityRole, new LambdaUpdateWrapper<AuthorityRole>().eq(AuthorityRole::getAuthority, authorityRole.getAuthority())
                .eq(AuthorityRole::getCode, authorityRole.getCode()));
        return authorityRole.getId();
    }
}
