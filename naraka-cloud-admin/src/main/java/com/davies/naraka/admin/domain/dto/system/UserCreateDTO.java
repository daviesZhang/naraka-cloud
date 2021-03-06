package com.davies.naraka.admin.domain.dto.system;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author davies
 * @date 2022/1/24 2:47 PM
 */
@Data
public class UserCreateDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String password;

}
