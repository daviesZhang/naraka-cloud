package com.davies.naraka.domain.dto.system;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/2/7 1:40 PM
 */
@Data
public class RoleDTO {

    private Integer id;

    private String name;

    private String code;

    private String remark;


    private String createdBy;

    private LocalDateTime createdTime;

    private String updatedBy;

    private LocalDateTime updatedTime;
}
