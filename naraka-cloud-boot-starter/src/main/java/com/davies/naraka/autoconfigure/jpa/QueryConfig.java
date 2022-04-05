package com.davies.naraka.autoconfigure.jpa;

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

    String alias() default "";

    boolean skip() default false;

    QueryFilterType filterType() default QueryFilterType.EQUALS;
}
