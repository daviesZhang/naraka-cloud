package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import org.jetbrains.annotations.Nullable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author davies
 * @date 2022/5/4 19:55
 */
public interface JpaQueryHandle {

    /**
     * @param column 查询的字段名
     * @param value  查询的值
     * @param field  查询的类字段,非类查询可能为空,比如map
     * @return true支持处理,
     */
    boolean support(QueryFilterType queryFilterType,
                    Object value,
                    String column,
                    @Nullable Field field);


    /**
     * @param queryFilterType
     * @param value
     * @param orders
     * @param column
     * @param root
     * @param cb
     * @param field
     * @param <T>
     * @return
     */
    <T> Predicate handle(QueryFilterType queryFilterType,
                         Object value,
                         List<Order> orders,
                         String column,
                         Path<T> root,
                         CriteriaBuilder cb,
                         @Nullable Field field);
}
