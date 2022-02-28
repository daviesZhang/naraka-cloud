package com.davies.naraka.cloud.common.annotation;

import java.lang.annotation.*;

/**
 * 暂时只支持字符串类型加解密
 * 被此注解标记的实体类及字段或QueryDTO 字段,在读写时进行加解密操作
 * 例如 {@link  com.davies.naraka.admin.domain.dto.system.UserQueryDTO} {@link  com.davies.naraka.admin.domain.entity.User}
 * 对于queryDTO在{@link com.davies.naraka.admin.common.QueryUtils}中使用
 * 对于entity类使用mybatis拦截器处理 {@link com.davies.naraka.admin.config.mybatis.ParamsCryptoInterceptor}
 * {@link com.davies.naraka.admin.config.mybatis.ResultCryptoInterceptor}
 * @author davies
 * @date 2022/1/30 9:17 PM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Crypto {


    /**
     * 加密key name
     * 如果不存在,直接使用字段名
     *
     */
    String name() default "";
}
