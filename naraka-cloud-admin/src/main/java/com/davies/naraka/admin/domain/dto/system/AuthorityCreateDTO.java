package com.davies.naraka.admin.domain.dto.system;

import com.davies.naraka.admin.domain.enums.AuthorityProcessorType;
import com.davies.naraka.admin.domain.enums.ResourceType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author davies
 * @date 2022/1/28 2:52 PM
 */
@Data
public class AuthorityCreateDTO {


    @NotBlank
    private String resource;

    /**
     * 资源类型如:URL,MENU
     */
    @NotNull
    private ResourceType resourceType;

    /**
     * 处理动作,如过滤,脱敏
     */
    @NotNull
    private AuthorityProcessorType processor;

    /**
     * 处理值,如字段名
     */
    @NotBlank
    private String processorValue;
}
