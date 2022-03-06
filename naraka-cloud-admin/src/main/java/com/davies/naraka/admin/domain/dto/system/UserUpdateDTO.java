package com.davies.naraka.admin.domain.dto.system;

import com.davies.naraka.admin.domain.enums.UserStatus;
import com.davies.naraka.admin.domain.enums.UserType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/1/24 2:47 PM
 */
@Data
public class UserUpdateDTO {

    @NotBlank
    private String username;

    @NotBlank
    private LocalDateTime passwordExpireTime;

    @NotBlank
    private UserType type;

    @NotBlank
    private UserStatus status;


    private String remark;


    private String password;

}
