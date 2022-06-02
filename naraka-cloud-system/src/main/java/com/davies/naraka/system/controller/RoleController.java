package com.davies.naraka.system.controller;

import com.davies.naraka.system.domain.dto.RoleCreateDTO;
import com.davies.naraka.system.domain.entity.SysRole;
import com.davies.naraka.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author davies
 * @date 2022/6/2 10:32
 */
@RestController
@Validated
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public String createRole(@RequestBody @Validated RoleCreateDTO create) {
        SysRole role = new SysRole();
        role.setId(create.getCode());
        role.setName(create.getName());
        roleService.createRole(role);
        return create.getCode();
    }


}
