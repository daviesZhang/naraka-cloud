package com.davies.naraka.admin.annotation;

import java.lang.annotation.*;

/**
 * @author davies
 * @date 2022/2/21 10:49 AM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface DistributedLock {

    /**
     * 锁key 支持 sqel 表达式,例如#user.getUsername()
     * @return
     */
    String key() default "";

    /**
     * 锁超时 毫秒
     * 默认3秒
     * @return
     */
    long leaseTime() default 3000L;


    /**
     * 取锁自旋时间
     * 默认没取到立即失败
     * @return
     */
    long waitTime() default 0L;

}
