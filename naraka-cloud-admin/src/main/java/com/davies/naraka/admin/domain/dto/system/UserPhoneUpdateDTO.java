package com.davies.naraka.admin.domain.dto.system;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author davies
 * @date 2022/1/24 2:47 PM
 */
@Data
public class UserPhoneUpdateDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String phone;

}
