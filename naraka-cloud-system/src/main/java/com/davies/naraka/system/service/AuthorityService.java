package com.davies.naraka.system.service;

import com.davies.naraka.system.domain.entity.SysAuthority;

import java.util.List;

/**
 * @author davies
 * @date 2022/6/5 21:10
 */
public interface AuthorityService {

    SysAuthority createAuthority(SysAuthority authority);


    void deleteAuthority(String id);


    void updateAuthority(SysAuthority authority);


    void resetRoleAuthority(String code, List<String> authorities);


    void insertRoleAuthority(String code, List<String> authorities);

}
