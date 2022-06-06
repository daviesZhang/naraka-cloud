package com.davies.naraka.system.controller;

import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.jpa.SQLExecuteHelper;
import com.davies.naraka.autoconfigure.jpa.SQLParamsProvider;
import com.davies.naraka.system.domain.dto.*;
import com.davies.naraka.system.domain.entity.SysRole;
import com.davies.naraka.system.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author davies
 * @date 2022/6/2 10:32
 */
@RestController
@Validated
@RequestMapping("/role")
public class RoleController extends BaseController {

    private final RoleService roleService;


    private final SQLExecuteHelper executeHelper;

    public RoleController(RoleService roleService, SQLExecuteHelper executeHelper) {
        this.roleService = roleService;
        this.executeHelper = executeHelper;
    }

    @PostMapping
    public String createRole(@RequestBody @Validated RoleCreateDTO create) {
        SysRole role = new SysRole();
        role.setCode(create.getCode());
        role.setName(create.getName());
        roleService.createRole(role);
        return create.getCode();
    }


    @PostMapping("/page")
    public PageDTO<RoleDTO> rolePage(@RequestBody QueryPageDTO<RoleQueryDTO> query) {
        Page<RoleDTO> userPage = executeHelper
                .getPage(SQLParamsProvider.query(query), SysRole.class)
                .map(sysUser -> ClassUtils.copyObject(sysUser, new RoleDTO()));
        return transform(userPage);
    }


    @PutMapping()
    public void update(@RequestBody @Validated RoleUpdateDTO update) {
        SysRole role = ClassUtils.copyObject(update, new SysRole());
        roleService.updateRole(role, update.getAuthorities());

    }

    @DeleteMapping()
    public void delete(@RequestParam List<String> code) {
        roleService.deleteRole(code);
    }
}
