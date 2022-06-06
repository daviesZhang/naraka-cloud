package com.davies.naraka.system.controller;

import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.jpa.SQLExecuteHelper;
import com.davies.naraka.system.domain.dto.*;
import com.davies.naraka.system.domain.entity.SysUser;
import com.davies.naraka.system.service.RoleService;
import com.davies.naraka.system.service.UserService;
import com.davies.naraka.system.service.security.CurrentUser;
import com.davies.naraka.system.service.security.SecurityService;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author davies
 * @date 2022/5/29 18:21
 */
@RestController
@Validated
@RequestMapping("/user")
public class UserController extends BaseController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;


    private final SQLExecuteHelper queryHelper;

    private final RoleService roleService;

    private final SecurityService securityService;

    public UserController(UserService userService,
                          SQLExecuteHelper queryHelper,
                          PasswordEncoder passwordEncoder,
                          RoleService roleService,
                          SecurityService securityService) {
        this.userService = userService;
        this.queryHelper = queryHelper;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.securityService = securityService;
    }


    @GetMapping("/me")
    public CurrentUser me(HttpServletRequest request) {
        return this.securityService.login(request.getRemoteUser());
    }

    @PostMapping
    public String createUser(@RequestBody @Validated UserCreateDTO create) {
        SysUser user = ClassUtils.copyObject(create, new SysUser());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.createUser(user, null, null).getId();
    }


    @PostMapping("/page")
    public PageDTO<UserDTO> userPage(@RequestBody QueryPageDTO<UserQueryDTO> query) {
        Page<UserDTO> userPage = queryHelper
                .getPage(queryParams(query), SysUser.class)
                .map(sysUser -> ClassUtils.copyObject(sysUser, new UserDTO()));
        return transform(userPage);
    }

    @PostMapping("/role")
    public void assignRole(@RequestBody @Validated UserRoleAssignDTO assignDTO) {
        roleService.resetUserRole(assignDTO.getUserId(), assignDTO.getRoles());

    }

    @PutMapping()
    public void updateUser(@RequestBody @Validated UserUpdateDTO update) {
        SysUser user = ClassUtils.copyObject(update, new SysUser());
        userService.updateUser(user, update.getRoles(), update.getTenements());
    }

}
