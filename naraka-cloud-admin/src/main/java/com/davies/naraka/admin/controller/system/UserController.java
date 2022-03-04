package com.davies.naraka.admin.controller.system;


import com.davies.naraka.admin.domain.dto.QueryPageDTO;
import com.davies.naraka.admin.domain.dto.system.UserCreateDTO;
import com.davies.naraka.admin.domain.dto.system.UserDTO;
import com.davies.naraka.admin.domain.dto.system.UserQueryDTO;
import com.davies.naraka.admin.domain.dto.system.UserRoleCreateDTO;
import com.davies.naraka.admin.domain.entity.User;
import com.davies.naraka.admin.domain.entity.UserRole;
import com.davies.naraka.admin.service.IUserRoleService;
import com.davies.naraka.admin.service.IUserService;
import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.mybatis.MyBatisQueryUtils;
import com.davies.naraka.cloud.common.domain.PageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public UserController(IUserService userService, IUserRoleService userRoleService, MyBatisQueryUtils queryUtils, PasswordEncoder passwordEncoder) {
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
