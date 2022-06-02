package com.davies.naraka.autoconfigure.annotation;

import com.davies.naraka.autoconfigure.enums.QueryFilterType;

import java.lang.annotation.*;

/**
 * @author davies
 * @date 2022/3/26 19:28
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface QueryConfig {


    QueryFilterType filterType() default QueryFilterType.EQ;
}
