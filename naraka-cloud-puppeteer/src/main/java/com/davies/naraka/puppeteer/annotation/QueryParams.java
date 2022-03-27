package com.davies.naraka.puppeteer.annotation;

import com.davies.naraka.autoconfigure.enums.QueryFilterType;

import java.lang.annotation.*;

/**
 * @author davies
 * @date 2022/3/26 19:28
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface QueryParams {

    String alias() default "";

    boolean skip() default false;

    QueryFilterType filterType() default QueryFilterType.EQUALS;
}
