package com.davies.naraka.system.service;

import com.davies.naraka.system.domain.entity.SysUser;

import java.util.List;

/**
 * @author davies
 * @date 2022/5/29 21:30
 */
public interface UserService {


    SysUser createUser(SysUser sysUser, List<String> roles, List<String> tenements);


    void updateUser(SysUser sysUser, List<String> roles, List<String> tenements);


}
