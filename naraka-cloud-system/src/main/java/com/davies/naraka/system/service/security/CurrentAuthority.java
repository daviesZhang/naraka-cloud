package com.davies.naraka.system.service.security;

import com.davies.naraka.system.domain.enums.AuthorityType;
import lombok.Data;

/**
 * @author davies
 * @date 2022/6/1 15:57
 */
@Data
public class CurrentAuthority {

    private String id;


    private String name;


    private String resource;


    private AuthorityType resourceType;


    private String data;
}
