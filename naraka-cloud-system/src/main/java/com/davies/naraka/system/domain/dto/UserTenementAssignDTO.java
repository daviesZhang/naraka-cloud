package com.davies.naraka.system.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author davies
 * @date 2022/6/5 23:08
 */
@Data
public class UserTenementAssignDTO {
    private String userId;


    private List<String> tenement;
}
