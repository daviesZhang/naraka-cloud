package com.davies.naraka.system.controller;

import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.jpa.SQLExecuteHelper;
import com.davies.naraka.autoconfigure.jpa.SQLParamsProvider;
import com.davies.naraka.system.domain.dto.QueryPageDTO;
import com.davies.naraka.system.domain.dto.RoleCreateDTO;
import com.davies.naraka.system.domain.dto.RoleDTO;
import com.davies.naraka.system.domain.dto.RoleQueryDTO;
import com.davies.naraka.system.domain.entity.SysRole;
import com.davies.naraka.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;


    @Autowired
    private SQLExecuteHelper executeHelper;

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

}
