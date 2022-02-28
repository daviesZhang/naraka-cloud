package com.davies.naraka.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.davies.naraka.admin.domain.enums.CrudType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 操作时间 
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 操作类型
     */
    private CrudType crudType;

    /**
     * 客户端ip
     */
    private String clientIp;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 数据类
     */
    private String dataClass;

    /**
     * 差异
     */
    private String difference;

    @TableField( fill = FieldFill.INSERT)
    private String createdBy;
    @TableField( fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField( fill = FieldFill.UPDATE)
    private String updatedBy;
    @TableField( fill = FieldFill.UPDATE)
    private LocalDateTime updatedTime;


}
