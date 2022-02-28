package com.davies.naraka.admin.domain.dto.system;

import com.davies.naraka.admin.domain.enums.UserStatus;
import com.davies.naraka.admin.domain.enums.UserType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/1/24 2:47 PM
 */
@Data
public class UserDTO {

    private String username;

    private String email;

    private String phone;

    private String password;

    private LocalDateTime passwordExpireTime;

    private String role;

    private String roleCode;

    private UserType type;

    private UserStatus status;

    private String remark;

    private String createdBy;

    private LocalDateTime createdTime;

    private String updatedBy;

    private LocalDateTime updatedTime;

}
