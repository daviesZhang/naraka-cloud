package com.davies.naraka.annotation;

import com.davies.naraka.domain.enums.QueryFilterType;

import java.lang.annotation.*;

/**
 * 查询条件运算规则
 * @see com.davies.naraka.domain.dto.system.UserQueryDTO
 * @see com.davies.naraka.common.QueryUtils
 * @author davies
 * @date 2022/1/30 10:56 AM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface QueryFilter {

    QueryFilterType[] types() default {QueryFilterType.EQUALS};


}
