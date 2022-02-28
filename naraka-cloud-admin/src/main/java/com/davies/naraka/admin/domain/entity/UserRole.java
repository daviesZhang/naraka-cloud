package com.davies.naraka.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

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
@TableName("t_user_role")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    private String username;

    private String code;

    private String remark;

    @TableField( fill = FieldFill.INSERT)
    private String createdBy;
    @TableField( fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField( fill = FieldFill.UPDATE)
    private String updatedBy;
    @TableField( fill = FieldFill.UPDATE)
    private LocalDateTime updatedTime;



}
