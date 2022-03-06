package com.davies.naraka.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("t_authority_role")
public class AuthorityRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    private Integer authority;

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
