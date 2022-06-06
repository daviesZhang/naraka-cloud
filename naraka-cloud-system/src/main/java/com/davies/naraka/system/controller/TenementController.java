package com.davies.naraka.system.controller;

import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.system.domain.dto.RoleCreateDTO;
import com.davies.naraka.system.domain.dto.TenementUpdateDTO;
import com.davies.naraka.system.domain.entity.SysTenement;
import com.davies.naraka.system.service.TenementService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author davies
 * @date 2022/6/2 11:05
 */
@RestController
@Validated
@RequestMapping("/tenement")
public class TenementController {

    private final TenementService tenementService;

    public TenementController(TenementService tenementService) {
        this.tenementService = tenementService;
    }

    @PostMapping
    public String create(@RequestBody @Validated RoleCreateDTO create) {
        SysTenement tenement = new SysTenement();
        tenement.setCode(create.getCode());
        tenement.setName(create.getName());
        tenementService.createTenement(tenement);
        return create.getCode();
    }


    @PutMapping()
    public void update(@RequestBody @Validated TenementUpdateDTO update) {
        SysTenement tenement = ClassUtils.copyObject(update, new SysTenement());
        tenementService.updateTenement(tenement, update.getRoles());
    }


}
