package com.davies.naraka.autoconfigure.jpa;

import javax.persistence.criteria.JoinType;
import java.lang.annotation.*;

/**
 * 使用JpaSpecificationUtils 构造查询时,查询对象字段上添加了此注解,意味需要join查询
 *
 * @author davies
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface JoinQuery {

    String attributeName() default "";

    JoinType joinType() default JoinType.LEFT;

}
