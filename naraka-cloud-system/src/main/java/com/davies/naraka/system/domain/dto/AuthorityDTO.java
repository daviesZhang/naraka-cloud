package com.davies.naraka.system.domain.dto;

import com.davies.naraka.system.domain.enums.AuthorityType;
import lombok.Data;

/**
 * @author davies
 * @date 2022/6/5 21:25
 */
@Data
public class AuthorityDTO {

    private String id;


    private String name;


    private String resource;


    private AuthorityType resourceType;


    private String data;
}
