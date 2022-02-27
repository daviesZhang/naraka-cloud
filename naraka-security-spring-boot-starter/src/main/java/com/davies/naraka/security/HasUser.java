package com.davies.naraka.security;

import java.lang.annotation.*;

/**
 * aop检查当前httpServletRequest是否包含remoteUser,如果不包含抛出异常
 * @author davies
 * @date 2022/2/27 12:42 PM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface HasUser {
}
