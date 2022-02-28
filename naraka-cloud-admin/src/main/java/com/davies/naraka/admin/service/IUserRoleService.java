package com.davies.naraka.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.davies.naraka.admin.domain.entity.UserRole;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
public interface IUserRoleService extends IService<UserRole> {


    /**
     * 创建用户和角色的关联关系
     * 如果已经存在则更新
     * @param userRole
     * @return ID
     */
    Integer createUserRole(UserRole userRole);



}
