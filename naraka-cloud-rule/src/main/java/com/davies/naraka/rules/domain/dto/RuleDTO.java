package com.davies.naraka.rules.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/3/19 19:30
 */
@Data
public class RuleDTO {


    private Long id;


    private String project;


    private String name;


    private String content;


    private String remark;


    private String createdBy;

    private LocalDateTime createdTime;

    private String updatedBy;

    private LocalDateTime updatedTime;
}
