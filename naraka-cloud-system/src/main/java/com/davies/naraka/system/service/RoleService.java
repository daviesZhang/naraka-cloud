package com.davies.naraka.system.service;

import com.davies.naraka.system.domain.entity.SysRole;

import java.util.List;

/**
 * @author davies
 * @date 2022/6/1 17:01
 */
public interface RoleService {


    SysRole createRole(SysRole role);

    /**
     * 删除用户所有角色
     *
     * @param userId
     */
    void deleteUserRole(String userId);


    void insertUserRole(String userId, List<String> roles);
}
