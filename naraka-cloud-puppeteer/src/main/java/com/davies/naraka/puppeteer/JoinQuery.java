package com.davies.naraka.puppeteer;

import javax.persistence.criteria.JoinType;
import java.lang.annotation.*;

/**
 * @author sycho
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface JoinQuery {

    String attributeName() default "";

    JoinType joinType() default JoinType.LEFT;

}
