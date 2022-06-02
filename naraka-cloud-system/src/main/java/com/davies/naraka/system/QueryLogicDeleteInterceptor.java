package com.davies.naraka.system;

import com.davies.naraka.autoconfigure.jpa.QueryParamsInterceptor;
import com.google.common.base.Strings;

import javax.persistence.Query;

/**
 * @author davies
 * @date 2022/6/1 08:47
 */
public class QueryLogicDeleteInterceptor<T> implements QueryParamsInterceptor<T> {

    private final String column;

    private final static String DEFAULT_COLUMN = "logicDelete";

    public QueryLogicDeleteInterceptor(String column) {
        this.column = Strings.isNullOrEmpty(column) ? DEFAULT_COLUMN : column;
    }

    public QueryLogicDeleteInterceptor() {
        this.column = this.DEFAULT_COLUMN;
    }

    @Override
    public String afterByWhere(T object, String sql) {
        return this.column + "=:logicDelete";
    }

    @Override
    public void queryConsumer(T object, Query query) {
        query.setParameter("logicDelete", false);
    }
}
