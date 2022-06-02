package com.davies.naraka.system.repository;

import com.davies.naraka.system.domain.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * @author davies
 * @date 2022/5/29 17:16
 */
@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
class SysRoleAuthorityRepositoryTest {


    @Autowired
    private SysUserRepository userRepository;

    @Test
    public void println() {
        List<SysUser> sysUser = userRepository.findAll();
        log.info(sysUser.toString());
    }



}
