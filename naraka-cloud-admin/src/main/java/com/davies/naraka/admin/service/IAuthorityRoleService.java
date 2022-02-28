package com.davies.naraka.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.davies.naraka.admin.domain.entity.AuthorityRole;

/**
 *
 * <p>
 *  服务类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
public interface IAuthorityRoleService extends IService<AuthorityRole> {
    /**
     * 创建角色和权限的关联
     * @param authorityRole
     * @return
     */
    Integer createAuthorityRole(AuthorityRole authorityRole);
}
