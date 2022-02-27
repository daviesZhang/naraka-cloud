package com.davies.naraka.controller.system;

import com.davies.naraka.common.ClassUtils;
import com.davies.naraka.common.QueryUtils;
import com.davies.naraka.domain.dto.PageDTO;
import com.davies.naraka.domain.dto.QueryPageDTO;
import com.davies.naraka.domain.dto.system.AuthorityCreateDTO;
import com.davies.naraka.domain.dto.system.AuthorityDTO;
import com.davies.naraka.domain.dto.system.AuthorityQueryDTO;
import com.davies.naraka.domain.entity.Authority;
import com.davies.naraka.service.IAuthorityService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

/**
 * @author davies
 * @date 2022/1/28 2:51 PM
 */
@RestController
@RequestMapping("/authority")
public class AuthorityController {

    private final IAuthorityService authorityService;


    private final QueryUtils queryUtils;

    public AuthorityController(IAuthorityService authorityService, QueryUtils queryUtils) {
        this.authorityService = authorityService;

        this.queryUtils = queryUtils;
    }


    @PostMapping
    public ResponseEntity<Void> createAuthority(@RequestBody AuthorityCreateDTO createAuthority) {
        Authority authority = ClassUtils.copyObject(createAuthority, new Authority());
        authorityService.createAuthority(authority);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除权限
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")

    public ResponseEntity<Void> deleteAuthority(@PathVariable Integer id) {
        authorityService.delete(Collections.singletonList(id));
        return ResponseEntity.ok().build();
    }


    /**
     * 根据条件获取authority列表
     *
     * @return List<AuthorityDTO>
     */
    @PostMapping("/list")
    public PageDTO<AuthorityDTO> authorityList(@RequestBody @Validated QueryPageDTO<AuthorityQueryDTO> queryPage) {
        return queryUtils.pageQuery(AuthorityDTO::new, queryPage, authorityService);
    }

}
