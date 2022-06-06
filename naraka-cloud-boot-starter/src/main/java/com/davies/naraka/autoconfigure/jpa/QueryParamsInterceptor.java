package com.davies.naraka.autoconfigure.jpa;

import javax.persistence.Query;

/**
 * @author davies
 * @date 2022/3/28 20:43
 */
public interface QueryParamsInterceptor<T> {


    /**
     * 查询条件构造完成后
     *
     * @param object 参数对象
     * @param sql    当前已经构造的sql
     * @return
     */
    String afterByWhere(T object, String sql);


    /**
     * 最后
     *
     * @param object
     * @param params
     * @return
     */
    default <R extends SQLParams> R after(T object, R params) {
        return params;
    }

    /**
     * 是否支持对当前Field的处理
     *
     * @param field
     * @return
     */
    default boolean support(Object field) {
        return true;
    }


    default void queryConsumer(T object, Query query) {
    }

}
