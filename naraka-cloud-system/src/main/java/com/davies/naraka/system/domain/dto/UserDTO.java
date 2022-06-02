package com.davies.naraka.system.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author davies
 * @date 2022/5/30 17:59
 */
@Data
public class UserDTO {

    private String id;


    private String username;


    private String email;


    private String phone;


    private String createdBy;


    private LocalDateTime createdDate;


    private LocalDateTime updatedDate;


    private String updatedBy;
}
