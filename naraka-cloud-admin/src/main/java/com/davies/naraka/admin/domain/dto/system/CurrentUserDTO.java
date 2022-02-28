package com.davies.naraka.admin.domain.dto.system;

import lombok.Data;
import org.reflections.serializers.Serializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author davies
 * @date 2022/1/24 2:47 PM
 */
@Data
public class CurrentUserDTO implements Serializable {

    private static final long serialVersionUID = 7143662939106101981L;
    private String username;


    private LocalDateTime passwordExpireTime;

    private LocalDateTime createdTime;

    /**
     * 拥有的接口和接口中已被过滤的字段
     * URL 过滤字段
     */
   private Map<String,String> authority;


}
