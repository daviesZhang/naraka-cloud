package com.davies.naraka.system.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author davies
 * @date 2022/6/2 10:34
 */
@Data
public class RoleUpdateDTO {


    private String code;


    private String name;


    private List<String> authorities;
}
