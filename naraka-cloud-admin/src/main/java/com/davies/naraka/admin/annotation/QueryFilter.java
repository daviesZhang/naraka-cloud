package com.davies.naraka.admin.annotation;

import com.davies.naraka.admin.domain.enums.QueryFilterType;

import java.lang.annotation.*;

/**
 * 查询条件运算规则
 * @see com.davies.naraka.admin.domain.dto.system.UserQueryDTO
 * @see com.davies.naraka.admin.common.QueryUtils
 * @author davies
 * @date 2022/1/30 10:56 AM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface QueryFilter {

    QueryFilterType[] types() default {QueryFilterType.EQUALS};


}
