package com.davies.naraka.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.davies.naraka.admin.domain.entity.Role;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
public interface IRoleService extends IService<Role> {


    /**
     * 创建角色
     * @param role
     * @return
     */
    Role createRole(Role role);


    /**
     * 根据角色 code删除角色列表
     * @param code
     */
    void delete(String code);




}
