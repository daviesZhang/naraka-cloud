package com.davies.naraka.system.service.security;

import com.davies.naraka.autoconfigure.security.SecurityHelper;
import com.davies.naraka.cloud.common.exception.NarakaException;
import com.davies.naraka.system.domain.entity.SysAuthority;
import com.davies.naraka.system.domain.entity.SysRole;
import com.davies.naraka.system.domain.entity.SysTenement;
import com.davies.naraka.system.domain.entity.SysUser;
import com.davies.naraka.system.repository.SysUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author davies
 * @date 2022/6/1 09:45
 */
@Component
@Slf4j
public class SecurityService {


    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ObjectMapper objectMapper;


    public CurrentUser login(String username) {
        return userRepository.findByUsername(username)
                .map(this::user)
                .orElseThrow(() -> new NarakaException("没有这个用户~"));
    }

    private CurrentUser user(SysUser sysUser) {
        List<SysTenement> sysTenements = sysUser.getTenements();
        Stream<SysRole> tenementRoles = sysTenements
                .stream().flatMap(sysTenement -> sysTenement.getRoles().stream());
        List<SysRole> sysRoles = Stream.concat(tenementRoles, sysUser.getRoles().stream())
                .collect(Collectors.toList());
        List<CurrentRole> roles = new ArrayList<>(sysRoles.size());
        List<CurrentAuthority> authorities = new ArrayList<>();
        List<CurrentTenement> tenements = new ArrayList<>(sysTenements.size());
        for (SysRole sysRole : sysRoles) {
            CurrentRole role = new CurrentRole();
            role.setCode(sysRole.getCode());
            role.setName(sysRole.getName());
            roles.add(role);
            for (SysAuthority authority : sysRole.getAuthorities()) {
                CurrentAuthority auth = new CurrentAuthority();
                auth.setData(authority.getData());
                auth.setId(authority.getId());
                auth.setName(authority.getName());
                auth.setResource(authority.getResource());
                auth.setResourceType(authority.getResourceType());
                authorities.add(auth);
            }
        }
        for (SysTenement sysTenement : sysTenements) {
            CurrentTenement tenement = new CurrentTenement();
            tenement.setCode(sysTenement.getCode());
            tenement.setName(sysTenement.getName());
            tenements.add(tenement);
        }
        CurrentUser currentUser = new CurrentUser();
        currentUser.setAuthorities(authorities);
        currentUser.setRoles(roles);
        currentUser.setTenements(tenements);
        currentUser.setId(sysUser.getId());
        currentUser.setUsername(sysUser.getUsername());
        try {
            redissonClient.getBucket(SecurityHelper.userAuthorityApiCacheKey(sysUser.getUsername())).set(objectMapper.writeValueAsString(authorities), 40, TimeUnit.MINUTES);
            redissonClient.getBucket("user:" + sysUser.getUsername()).set(objectMapper.writeValueAsString(currentUser), 40, TimeUnit.MINUTES);
            return currentUser;
        } catch (Exception e) {
            throw new NarakaException(e);
        }
    }

}
