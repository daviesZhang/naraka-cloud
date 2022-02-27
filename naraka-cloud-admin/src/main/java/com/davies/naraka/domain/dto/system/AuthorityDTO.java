package com.davies.naraka.domain.dto.system;

import com.davies.naraka.domain.enums.AuthorityProcessorType;
import com.davies.naraka.domain.enums.ResourceType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/2/10 5:36 PM
 */
@Data
public class AuthorityDTO {

    private Integer id;


    private String resource;

    /**
     * 资源类型如:URL,MENU
     */

    private ResourceType resourceType;

    /**
     * 处理动作,如过滤,脱敏
     */

    private AuthorityProcessorType processor;

    /**
     * 处理值,如字段名
     */

    private String processorValue;
    /**
     * 备注
     */
    private String remark;

    private String createdBy;

    private LocalDateTime createdTime;

    private String updatedBy;

    private LocalDateTime updatedTime;
}
