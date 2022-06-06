package com.davies.naraka.system.controller;

import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.jpa.SQLExecuteHelper;
import com.davies.naraka.autoconfigure.jpa.SQLParamsProvider;
import com.davies.naraka.system.domain.dto.AuthorityCreateDTO;
import com.davies.naraka.system.domain.dto.AuthorityDTO;
import com.davies.naraka.system.domain.dto.AuthorityQueryDTO;
import com.davies.naraka.system.domain.dto.QueryPageDTO;
import com.davies.naraka.system.domain.entity.SysAuthority;
import com.davies.naraka.system.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author davies
 * @date 2022/6/5 21:09
 */
@RestController
@Validated
@RequestMapping("/authority")
public class AuthorityController extends BaseController {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private SQLExecuteHelper executeHelper;


    @PostMapping
    public String createAuthority(@RequestBody @Validated AuthorityCreateDTO create) {
        SysAuthority authority = ClassUtils.copyObject(create, new SysAuthority());
        authorityService.createAuthority(authority);
        return authority.getId();
    }


    @PostMapping("/page")
    public PageDTO<AuthorityDTO> authorityPage(@RequestBody QueryPageDTO<AuthorityQueryDTO> query) {
        Page<AuthorityDTO> userPage = executeHelper
                .getPage(SQLParamsProvider.query(query), SysAuthority.class)
                .map(sysUser -> ClassUtils.copyObject(sysUser, new AuthorityDTO()));
        return transform(userPage);
    }


}
