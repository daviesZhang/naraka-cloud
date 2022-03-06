package com.davies.naraka.admin.domain.dto.system;

import com.davies.naraka.admin.domain.enums.AuthorityProcessorType;
import com.davies.naraka.admin.domain.enums.ResourceType;
import lombok.Data;

/**
 * @author davies
 * @date 2022/2/10 5:36 PM
 */
@Data
public class RoleAuthorityDTO {

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


}
