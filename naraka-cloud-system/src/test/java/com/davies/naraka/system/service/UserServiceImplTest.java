package com.davies.naraka.system.service;

import com.davies.naraka.autoconfigure.redis.RedisIdGenerate;
import com.davies.naraka.system.BaseTest;
import com.davies.naraka.system.config.IdGenerator;
import com.davies.naraka.system.domain.entity.*;
import com.davies.naraka.system.domain.enums.AuthorityType;
import com.davies.naraka.system.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author davies
 * @date 2022/5/29 21:57
 */
@Slf4j
class UserServiceImplTest extends BaseTest {


    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IdGenerator idGenerator;


    @Autowired
    private SysTenementRepository tenementRepository;

    @Autowired
    private SysRoleRepository roleRepository;

    @Autowired
    private SysRoleAuthorityRepository roleAuthorityRepository;
    @Autowired
    private SysUserRoleRepository userRoleRepository;

    @Autowired
    private SysUserTenementRepository userTenementRepository;

    @Autowired
    private SysTenementRoleRepository tenementRoleRepository;

    @Autowired
    private SysAuthorityRepository authorityRepository;
    @Autowired
    private RedisIdGenerate redisIdGenerate;

    @Test
    @Transactional(rollbackFor = Exception.class)
    @Rollback(value = false)
    void createUser() {

        SysAuthority authority = new SysAuthority();
        authority.setCreatedBy("root");
        authority.setName("刷新token");
        authority.setResourceType(AuthorityType.API);
        authority.setResource("get /system/token/refresh");
        authorityRepository.save(authority);
        SysRole sysRole = new SysRole();
        sysRole.setName("超级管理员");
        sysRole.setId("ADMIN");
        sysRole.setCreatedBy("root");
        // sysRole.setAuthorities(Collections.singletonList(authority));
        roleRepository.save(sysRole);
        SysRoleAuthority roleAuthority = new SysRoleAuthority();
        SysRoleAuthorityId roleAuthorityId = new SysRoleAuthorityId(sysRole.getId(), authority.getId());
        roleAuthority.setId(roleAuthorityId);
        roleAuthority.setCreatedBy("root");
        roleAuthorityRepository.save(roleAuthority);

        SysTenement tenement = new SysTenement();
        tenement.setDesc("总经销商");
        tenement.setName("总经销商");
        tenement.setCreatedBy("root");
        tenementRepository.save(tenement);
        SysUser sysUser = new SysUser();
        sysUser.setUsername("root");
        sysUser.setPassword(passwordEncoder.encode("123456"));
        sysUser.setPhone("18566242312");
        sysUser.setCreatedBy("root");
        // sysUser.setTenements(Collections.singletonList(tenement));
        // sysUser.setRoles(Collections.singletonList(sysRole));

        userService.createUser(sysUser);

        SysUserRole sysUserRole = new SysUserRole();
        SysUserRoleId userRoleId = new SysUserRoleId(sysUser.getId(), sysRole.getId());
        sysUserRole.setId(userRoleId);
        sysUserRole.setCreatedBy("root");
        userRoleRepository.save(sysUserRole);

        SysUserTenement userTenement = new SysUserTenement();
        SysUserTenementId userTenementId = new SysUserTenementId(sysUser.getId(), tenement.getId());
        userTenement.setId(userTenementId);
        userTenement.setCreatedBy("root");
        userTenementRepository.save(userTenement);


    }

    @Test
    public void print() throws InterruptedException {

        for (int i = 0; i < 100; i++) {
            String id = redisIdGenerate.nextUUID(this);
            log.info("id====" + id);
        }
        Thread.sleep(1000 * 2);

        for (int i = 0; i < 100; i++) {
            String id = redisIdGenerate.nextUUID(this);
            log.info("id====" + id);
        }

    }
}
