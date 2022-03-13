package com.davies.naraka.autoconfigure.security;

import java.lang.annotation.*;

/**
 * aop检查当前能否获取到user,如果不能抛出异常
 *
 * @author davies
 * @date 2022/2/27 12:42 PM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface HasUser {
}
