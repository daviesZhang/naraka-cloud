package com.davies.naraka.domain.dto.system;

import lombok.Data;

/**
 * @author davies
 * @date 2022/1/24 2:47 PM
 */
@Data
public class UserCreateDTO {

    private String username;


    private String email;


    private String phone;


    private String password;

}
