package com.davies.naraka.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.davies.naraka.admin.domain.bo.UserAndRoleBO;
import com.davies.naraka.admin.domain.entity.*;
import com.davies.naraka.admin.domain.enums.ResourceType;
import com.davies.naraka.admin.mapper.UserMapper;
import com.davies.naraka.admin.service.*;
import com.davies.naraka.admin.service.exception.UserEmailExistsException;
import com.davies.naraka.admin.service.exception.UserNameExistsException;
import com.davies.naraka.admin.service.exception.UserPhoneExistsException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final IUserRoleService userRoleService;
    private final IAuthorityRoleService authorityRoleService;

    private final IRoleService roleService;

    private final IAuthorityService authorityService;

    public UserServiceImpl(IUserRoleService userRoleService, IAuthorityRoleService authorityRoleService,
                           IRoleService roleService, IAuthorityService authorityService) {
        this.userRoleService = userRoleService;
        this.authorityRoleService = authorityRoleService;
        this.roleService = roleService;
        this.authorityService = authorityService;
    }


    @Override
    public User createUser(User user) {
        findUserByUsername(user.getUsername())
                .map(User::getUsername)
                .ifPresent(username -> {
                    throw new UserNameExistsException(String.format("createUser username:[%s] username exists", username));
                });

        findUserByPhone(user.getPhone())
                .map(User::getUsername)
                .ifPresent(username -> {
                    throw new UserPhoneExistsException(String.format("createUser username:[%s] phone exists", username));
                });

        findUserByEmail(user.getEmail())
                .map(User::getUsername)
                .ifPresent(username -> {
                    throw new UserEmailExistsException(String.format("createUser username:[%s] email exists", username));
                });
        save(user);
        log.info("create user [{}] success", user.getUsername());
        return user;
    }

    @Override
    public List<Authority> getUserAuthorityList(@NotNull String username, ResourceType resourceType) {
        List<String> code = userRoleService.list(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUsername, username))
                .stream().map(UserRole::getCode).collect(Collectors.toList());
        if (code.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> ids = authorityRoleService.list(new LambdaQueryWrapper<AuthorityRole>().in(AuthorityRole::getCode, code))
                .stream().map(AuthorityRole::getAuthority).collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Authority> lambdaQueryWrapper = new LambdaQueryWrapper<Authority>().in(Authority::getId, ids);
        if (resourceType != null) {
            lambdaQueryWrapper.eq(Authority::getResourceType, resourceType);
        }
        return authorityService.list(lambdaQueryWrapper);
    }

    @Override
    public List<Role> getUserRoleList(@NotNull String username) {
        List<String> code = userRoleService.list(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUsername, username))
                .stream().map(UserRole::getCode).collect(Collectors.toList());
        if (code.isEmpty()) {
            return Collections.emptyList();
        }
        return roleService.list(new LambdaQueryWrapper<Role>().in(Role::getCode, code));
    }

    @Override
    public Optional<User> findUser(@NotNull Wrapper<User> queryWrapper) {
        return Optional.ofNullable(getOne(queryWrapper));
    }

    @Override
    public Optional<User> findUserByUsername(@NotNull String username) {

        return this.findUser(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public Optional<User> findUserById(int id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<User> findUserByPhone(@NotNull String phone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getPhone, phone);
        return this.findUser(queryWrapper);
    }

    @Override
    public Optional<User> findUserByEmail(@NotNull String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getEmail, email);
        return this.findUser(queryWrapper);
    }

    @Override
    public Page<UserAndRoleBO> userAndRolePage(Page<UserAndRoleBO> page, Wrapper<User> wrapper) {
        return baseMapper.selectUserAndRolePage(page, wrapper);
    }
}
