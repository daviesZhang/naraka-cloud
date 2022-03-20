package com.davies.naraka.rules.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author davies
 * @date 2022/3/19 19:06
 */
@Data
public class RuleCreateDTO {


    @NotBlank
    private String project;

    @NotBlank
    private String name;

    @NotBlank
    private String content;


    private String remark;
}
