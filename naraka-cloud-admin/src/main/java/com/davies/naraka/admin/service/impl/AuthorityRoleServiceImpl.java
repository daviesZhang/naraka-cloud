package com.davies.naraka.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.davies.naraka.admin.domain.entity.AuthorityRole;
import com.davies.naraka.admin.mapper.AuthorityRoleMapper;
import com.davies.naraka.admin.service.IAuthorityRoleService;
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

    @Override
    public void removeAuthorityRole(Integer authority, String code) {
        remove(new QueryWrapper<>(new AuthorityRole().setAuthority(authority).setCode(code)));

    }


}
