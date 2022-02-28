package com.davies.naraka.admin.domain.dto.system;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author davies
 * @date 2022/1/28 2:48 PM
 */
@Data
public class RoleCreateDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String remark;
}
