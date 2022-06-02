package com.davies.naraka.system.service.security;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author davies
 * @date 2022/6/1 15:01
 */
@Data
public class CurrentUser implements Serializable {


    private String id;


    private String username;


    private List<CurrentRole> roles = new ArrayList<>();

    private List<CurrentAuthority> authorities = new ArrayList<>();

    private List<CurrentTenement> tenements = new ArrayList<>();


}
