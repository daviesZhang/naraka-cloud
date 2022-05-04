package com.davies.naraka.autoconfigure.annotation;

import java.lang.annotation.*;

/**
 * 在Mybatis应用中
 * 在实体类上添加此注解,表明此类中有需要加解密的字段,
 * 同时在字段上添加此字段,在数据入库出库时mybatis拦截器会自动处理加解密
 * 参见 ParamsCryptoInterceptor 和 ResultCryptoInterceptor
 * 在 查询字段上添加了此注解,使用 MyBatisQueryUtils 或 JpaSpecificationUtils 构造查询参数时,会自动处理需要加密的情况
 *
 * @author davies
 * @date 2022/1/30 9:17 PM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Crypto {


    /**
     * 加密key name
     * 如果不存在,直接使用字段名
     */
    String value() default "";
}
