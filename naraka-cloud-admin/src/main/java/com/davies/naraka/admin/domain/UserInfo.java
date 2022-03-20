package com.davies.naraka.admin.domain;

import com.davies.naraka.admin.domain.enums.UserStatus;
import com.davies.naraka.admin.domain.enums.UserType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author davies
 * @date 2022/3/20 19:24
 */
@Data
public class UserInfo implements Serializable {


    private String username;

    private String email;

    private String phone;

    private UserType type;

    private UserStatus status;

    private String role;


    private String twoVerification;

    private String remark;

    private LocalDateTime passwordExpireTime;

    /**
     * 拥有的接口和接口中已被过滤的字段
     * URL 过滤字段
     */
    private Map<String, String> api;

    /**
     * 序列化给客户端时需要处理的权限
     * resource->value->bean name
     */
    private Map<String, Map<String, Set<String>>> authoritySerialize;


    /**
     * 获取结果集时,对行数据的过滤
     * resource-> bean name -> 参数
     */
    private Map<String, Map<String, Set<String>>> authorityRows;

    /**
     * 用户的组信息,由小到大
     */
    private List<String> group;


}
