package com.davies.naraka.system.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author davies
 * @date 2022/5/30 17:49
 */
@Data
public class UserCreateDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;


    private String email;


    private String phone;

}
