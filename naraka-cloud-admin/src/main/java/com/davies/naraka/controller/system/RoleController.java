package com.davies.naraka.controller.system;

import com.davies.naraka.common.ClassUtils;
import com.davies.naraka.common.QueryUtils;
import com.davies.naraka.domain.dto.PageDTO;
import com.davies.naraka.domain.dto.QueryPageDTO;
import com.davies.naraka.domain.dto.system.*;
import com.davies.naraka.domain.entity.AuthorityRole;
import com.davies.naraka.domain.entity.Role;
import com.davies.naraka.service.IAuthorityRoleService;
import com.davies.naraka.service.IRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author davies
 * @date 2022/1/28 2:42 PM
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private final IRoleService roleService;
    private final IAuthorityRoleService authorityRoleService;

    private final QueryUtils queryUtils;

    public RoleController(IRoleService roleService, IAuthorityRoleService authorityRoleService, QueryUtils queryUtils) {
        this.roleService = roleService;
        this.authorityRoleService = authorityRoleService;
        this.queryUtils = queryUtils;
    }


    @PostMapping
    public Integer createRole(@RequestBody @Validated RoleCreateDTO createRole){
        Role role = ClassUtils.copyObject(createRole, new Role());
        roleService.createRole(role);
        return role.getId();
    }


    @DeleteMapping("/{code}")
    public void deleteRole(@PathVariable String code){
        roleService.delete(code);
    }



    /**
     * 根据条件获取roles列表
     *
     * @return List<UserDTO>
     */
    @PostMapping("/list")
    public PageDTO<RoleDTO> roles(@RequestBody @Validated QueryPageDTO<RoleQueryDTO> query) {
        return queryUtils.pageQuery(RoleDTO::new, query, roleService);
    }


    /**
     * 分配权限到角色
     * @param createAuthorityRole
     * @return
     */
    @PostMapping("/assign")
    public ResponseEntity<Void> assignAuthority(@RequestBody @Validated AuthorityRoleCreateDTO createAuthorityRole) {
        AuthorityRole authorityRole = ClassUtils.copyObject(createAuthorityRole, new AuthorityRole());
        authorityRoleService.createAuthorityRole(authorityRole);
        return ResponseEntity.ok().build();
    }




}
