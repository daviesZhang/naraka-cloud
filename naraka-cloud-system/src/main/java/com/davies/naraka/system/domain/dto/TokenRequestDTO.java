package com.davies.naraka.system.domain.dto;

import lombok.Data;

/**
 * @author davies
 * @date 2022/5/31 17:48
 */
@Data
public class TokenRequestDTO {

    private String principal;

    private String password;
}
