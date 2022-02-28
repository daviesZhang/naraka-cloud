package com.davies.naraka.admin.annotation;

import java.lang.annotation.*;

/**
 * 查询sql拼接时字段的名字
 * 比如 u.username
 * @author davies
 * @date 2022/2/7 8:20 PM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface ColumnName {

    String name() default "";
}
