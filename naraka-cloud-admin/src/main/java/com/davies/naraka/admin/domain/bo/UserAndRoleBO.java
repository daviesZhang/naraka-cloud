package com.davies.naraka.admin.domain.bo;

import com.davies.naraka.cloud.common.annotation.Crypto;
import com.davies.naraka.admin.domain.enums.UserStatus;
import com.davies.naraka.admin.domain.enums.UserType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/2/7 9:52 PM
 */
@Data
@Crypto
public class UserAndRoleBO {
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

    private String role;

    private String roleCode;

    /**
     * 两步验证的secret,不为空则需要验证
     */
    private String twoVerification;

    private String remark;

    private String createdBy;

    private LocalDateTime createdTime;

    private String updatedBy;

    private LocalDateTime updatedTime;
}
