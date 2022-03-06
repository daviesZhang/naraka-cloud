package com.davies.naraka.admin.controller.system;

import com.davies.naraka.admin.domain.dto.QueryPageDTO;
import com.davies.naraka.admin.domain.dto.system.AuthorityCreateDTO;
import com.davies.naraka.admin.domain.dto.system.AuthorityDTO;
import com.davies.naraka.admin.domain.dto.system.AuthorityQueryDTO;
import com.davies.naraka.admin.domain.dto.system.AuthorityUpdateDTO;
import com.davies.naraka.admin.domain.entity.Authority;
import com.davies.naraka.admin.service.IAuthorityService;
import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.mybatis.MyBatisQueryUtils;
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


    private final MyBatisQueryUtils queryUtils;

    public AuthorityController(IAuthorityService authorityService, MyBatisQueryUtils queryUtils) {
        this.authorityService = authorityService;

        this.queryUtils = queryUtils;
    }


    @PostMapping
    public ResponseEntity<Void> createAuthority(@RequestBody @Validated AuthorityCreateDTO createAuthority) {
        Authority authority = ClassUtils.copyObject(createAuthority, new Authority());
        authorityService.createAuthority(authority);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateAuthority(@RequestBody @Validated AuthorityUpdateDTO updateDTO) {
        Authority authority = ClassUtils.copyObject(updateDTO, new Authority());
        authorityService.updateById(authority);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除权限
     *
     * @param id
     * @return
     */
    @DeleteMapping()
    public ResponseEntity<Void> deleteAuthority(@RequestParam Integer id) {
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
