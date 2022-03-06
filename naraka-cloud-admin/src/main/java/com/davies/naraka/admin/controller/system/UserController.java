package com.davies.naraka.admin.controller.system;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.davies.naraka.admin.domain.dto.QueryPageDTO;
import com.davies.naraka.admin.domain.dto.system.*;
import com.davies.naraka.admin.domain.entity.User;
import com.davies.naraka.admin.domain.entity.UserRole;
import com.davies.naraka.admin.service.IUserRoleService;
import com.davies.naraka.admin.service.IUserService;
import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.mybatis.MyBatisQueryUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/1/25 3:46 PM
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {


    private final IUserService userService;


    private final IUserRoleService userRoleService;


    private final MyBatisQueryUtils queryUtils;

    @Value("${user.password.timeout:365}")
    private Integer passwordExpireDay;

    private final PasswordEncoder passwordEncoder;

    public UserController(IUserService userService,
                          IUserRoleService userRoleService,
                          MyBatisQueryUtils queryUtils,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.queryUtils = queryUtils;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Validated UserCreateDTO createUser) {
        User user = ClassUtils.copyObject(createUser, new User());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordExpireTime(LocalDateTime.now().plusDays(passwordExpireDay));
        userService.createUser(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateUser(@RequestBody @Validated UserUpdateDTO updateDTO) {
        if (Strings.isNullOrEmpty(updateDTO.getPassword())) {
            updateDTO.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        User user = ClassUtils.copyObject(updateDTO, new User());
        userService.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/phone")
    public ResponseEntity<Void> updateUserPhone(@RequestBody @Validated UserPhoneUpdateDTO updateDTO) {
        User user = ClassUtils.copyObject(updateDTO, new User());
        userService.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/email")
    public ResponseEntity<Void> updateUserEmail(@RequestBody @Validated UserEmailUpdateDTO updateDTO) {
        User user = ClassUtils.copyObject(updateDTO, new User());
        userService.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        return ResponseEntity.ok().build();
    }
//

    /**
     * 根据条件获取users列表
     *
     * @return List<UserDTO>
     */

    @PostMapping("/list")
    public PageDTO<UserDTO> users(@RequestBody @Validated QueryPageDTO<UserQueryDTO> queryUser) {
        return queryUtils.pageQuery(UserDTO::new, queryUser, userService::userAndRolePage);
    }


    @GetMapping("/phone")
    public String fullPhone(@RequestParam String username) {
        return userService.findUserByUsername(username).map(User::getPhone).orElse(null);
    }

    @GetMapping("/email")
    public String fullEmail(@RequestParam String username) {
        return userService.findUserByUsername(username).map(User::getEmail).orElse(null);
    }

    /**
     * 分配角色到用户
     *
     * @param createUserRole
     * @return
     */

    @PostMapping("/assign")
    public Integer assignRole(@RequestBody @Validated UserRoleCreateDTO createUserRole) {
        UserRole role = ClassUtils.copyObject(createUserRole, new UserRole());
        userRoleService.createUserRole(role);
        return role.getId();
    }


}
