package com.davies.naraka.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.davies.naraka.admin.domain.enums.AuthorityProcessorType;
import com.davies.naraka.admin.domain.enums.ResourceType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_authority")
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 资源,如get /api/login
     */
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

    @TableField( fill = FieldFill.INSERT)
    private String createdBy;
    @TableField( fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField( fill = FieldFill.UPDATE)
    private String updatedBy;
    @TableField( fill = FieldFill.UPDATE)
    private LocalDateTime updatedTime;


}
