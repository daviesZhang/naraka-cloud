package com.davies.naraka.autoconfigure.jpa;

import org.springframework.data.domain.Pageable;

import javax.persistence.Query;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author davies
 * @date 2022/3/28 19:11
 */
public class QueryParams {


    private final String querySql;

    private final Pageable pageable;

    private final Function<Query, Query> queryConsumer;


    public QueryParams(String querySql, Function<Query, Query> queryConsumer, Pageable pageable) {
        this.querySql = querySql;
        this.queryConsumer = queryConsumer == null ? UnaryOperator.identity() : queryConsumer;
        this.pageable = pageable;
    }


    public String getQuerySql() {
        return querySql;
    }


    public Pageable getPageable() {
        return pageable;
    }


    public Function<Query, Query> getQueryConsumer() {
        return queryConsumer;
    }


}
