package com.davies.naraka.domain.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/2/10 3:41 PM
 */
@Data
public class MenuBO {

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
