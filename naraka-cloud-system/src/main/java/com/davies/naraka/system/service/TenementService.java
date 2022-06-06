package com.davies.naraka.system.service;

import com.davies.naraka.system.domain.entity.SysTenement;

import java.util.List;

/**
 * @author davies
 * @date 2022/6/2 10:59
 */
public interface TenementService {


    SysTenement createTenement(SysTenement tenement);

    /**
     * 删除用户所有角色
     *
     * @param userId
     */
    void deleteUserTenement(String userId);


    void insertUserTenement(String userId, List<String> tenementId);


    void updateTenement(SysTenement tenement, List<String> roles);
}
