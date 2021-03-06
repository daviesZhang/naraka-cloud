package com.davies.naraka.admin.domain.dto.system;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/2/10 3:39 PM
 */
@Data
public class MenuDTO {


    /**
     * 上级
     */
    private Integer parent;

    private Integer id;

    /**
     * URL
     */
    private String url;

    /**
     * CODE标识
     */
    private String code;


    /**
     * 备注
     */
    private String remark;

    private String createdBy;

    private LocalDateTime createdTime;

    private String updatedBy;

    private LocalDateTime updatedTime;
}
