package com.davies.naraka.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.davies.naraka.annotation.Crypto;
import com.davies.naraka.domain.enums.UserStatus;
import com.davies.naraka.domain.enums.UserType;
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
@TableName(value="t_user")
@Crypto
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    private String username;

    @Crypto
    private String email;

    @Crypto
    private String phone;

    private String password;

    private LocalDateTime passwordExpireTime;

    private UserType type;

    private UserStatus status;

    /**
     * 两步验证的secret,不为空则需要验证
     */
    private String twoVerification;

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
