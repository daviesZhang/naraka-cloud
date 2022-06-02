package com.davies.naraka.system.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author davies
 * @date 2022/6/1 16:54
 */
@Data
public class UserUpdateDTO {

    @NotBlank
    private String id;


    private String email;


    private String phone;


    private List<String> roles;

    private List<String> tenements;


}
