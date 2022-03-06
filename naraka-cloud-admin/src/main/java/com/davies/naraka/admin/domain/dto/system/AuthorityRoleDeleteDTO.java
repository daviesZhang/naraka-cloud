package com.davies.naraka.admin.domain.dto.system;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author davies
 * @date 2022/1/28 2:52 PM
 */
@Data
public class AuthorityRoleDeleteDTO {

    @NotNull
    private Integer authority;
    @NotBlank
    private String code;


}
