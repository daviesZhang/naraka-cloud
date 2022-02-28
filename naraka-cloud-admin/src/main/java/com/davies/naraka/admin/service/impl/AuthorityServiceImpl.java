package com.davies.naraka.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.davies.naraka.admin.mapper.AuthorityMapper;
import com.davies.naraka.admin.domain.entity.Authority;
import com.davies.naraka.admin.domain.entity.AuthorityRole;
import com.davies.naraka.admin.service.IAuthorityRoleService;
import com.davies.naraka.admin.service.IAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
@Service
public class AuthorityServiceImpl extends ServiceImpl<AuthorityMapper, Authority> implements IAuthorityService {

    @Autowired
    private IAuthorityRoleService authorityRoleService;

    @Override
    public void createAuthority(Authority authority) {
        LambdaUpdateWrapper<Authority> lambdaQueryWrapper = new LambdaUpdateWrapper<Authority>()
                .eq(Authority::getResource, authority.getResource())
                .eq(Authority::getResourceType, authority.getResourceType());
        if (authority.getProcessor() == null) {
            lambdaQueryWrapper.isNull(Authority::getProcessor);
        } else {
            lambdaQueryWrapper.eq(Authority::getProcessor, authority.getProcessor());
        }
        saveOrUpdate(authority, lambdaQueryWrapper);


    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void delete(List<Integer> ids) {
        baseMapper.deleteBatchIds(ids);
        authorityRoleService.remove(new LambdaQueryWrapper<AuthorityRole>()
                .in(AuthorityRole::getAuthority, ids));
    }

    @Override
    public Page<Authority> authorityList(Page<Authority> page, Wrapper<Authority> wrapper) {
        return baseMapper.selectAuthorityPage(page, wrapper);
    }
}
