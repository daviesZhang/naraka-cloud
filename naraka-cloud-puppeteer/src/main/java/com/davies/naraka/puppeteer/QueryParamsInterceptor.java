package com.davies.naraka.puppeteer;

import javax.persistence.Query;
import java.util.function.UnaryOperator;

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
     * @param queryParams
     * @return
     */
    default QueryParams after(T object, QueryParams queryParams) {
        return queryParams;
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


    default UnaryOperator<Query> queryConsumer(UnaryOperator<Query> operator) {
        return operator;
    }

}
