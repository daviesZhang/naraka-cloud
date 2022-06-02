package com.davies.naraka.admin.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.davies.naraka.admin.domain.dto.QueryPageDTO;
import com.davies.naraka.admin.domain.dto.system.*;
import com.davies.naraka.admin.domain.entity.AuthorityRole;
import com.davies.naraka.admin.domain.entity.Role;
import com.davies.naraka.admin.service.IAuthorityRoleService;
import com.davies.naraka.admin.service.IAuthorityService;
import com.davies.naraka.admin.service.IRoleService;
import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.autoconfigure.mybatis.MyBatisQueryUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author davies
 * @date 2022/1/28 2:42 PM
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private final IRoleService roleService;
    private final IAuthorityRoleService authorityRoleService;
    private final IAuthorityService authorityService;

    private final MyBatisQueryUtils queryUtils;

    public RoleController(IRoleService roleService, IAuthorityRoleService authorityRoleService, IAuthorityService authorityService, MyBatisQueryUtils queryUtils) {
        this.roleService = roleService;
        this.authorityRoleService = authorityRoleService;
        this.authorityService = authorityService;
        this.queryUtils = queryUtils;
    }


    @PostMapping
    public Integer createRole(@RequestBody @Validated RoleCreateDTO createRole) {
        Role role = ClassUtils.copyObject(createRole, new Role());
        roleService.createRole(role);
        return role.getId();
    }


    @DeleteMapping()
    public void deleteRole(@RequestParam String code) {
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


    @PostMapping("/authority")
    public PageDTO<RoleAuthorityDTO> authority(@RequestBody @Validated QueryPageDTO<RoleAuthorityQueryDTO> query) {
        RoleAuthorityQueryDTO queryDTO = query.getQuery();
        List<Integer> authorityList = authorityRoleService
                .list(new LambdaQueryWrapper<AuthorityRole>()
                        .eq(AuthorityRole::getCode, queryDTO.getCode()))
                .stream().map(AuthorityRole::getAuthority).collect(Collectors.toList());
        if (queryDTO.getAssign()) {
            if (authorityList.isEmpty()) {
                return new PageDTO<>(query.getCurrent(), query.getSize(), 0, Collections.emptyList());
            }
            queryDTO.setId(new QueryField<>(QueryFilterType.CONTAINS, authorityList));
        } else {
            if (authorityList.isEmpty()) {
                queryDTO.setId(null);
            } else {
                queryDTO.setId(new QueryField<>(QueryFilterType.NOT_CONTAINS, authorityList));
            }
        }
        return queryUtils.pageQuery(RoleAuthorityDTO::new, query, authorityService);
    }

    /**
     * 分配权限到角色
     *
     * @param createAuthorityRole
     * @return
     */
    @PostMapping("/assign")
    public ResponseEntity<Void> assignAuthority(@RequestBody @Validated AuthorityRoleCreateDTO createAuthorityRole) {
        AuthorityRole authorityRole = ClassUtils.copyObject(createAuthorityRole, new AuthorityRole());
        authorityRoleService.createAuthorityRole(authorityRole);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除 权限 角色关系
     *
     * @param authorityRoleDTO
     * @return
     */
    @PostMapping("/unAssign")
    public ResponseEntity<Void> unAssignAuthority(@RequestBody @Validated AuthorityRoleDeleteDTO authorityRoleDTO) {
        authorityRoleService.removeAuthorityRole(authorityRoleDTO.getAuthority(), authorityRoleDTO.getCode());
        return ResponseEntity.ok().build();
    }
}
