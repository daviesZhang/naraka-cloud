package com.davies.naraka.autoconfigure.annotation;


import java.lang.annotation.*;

/**
 * 跳过这个字段的查询
 *
 * @author davies
 * @date 2022/1/30 10:56 AM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface QuerySkip {


}
