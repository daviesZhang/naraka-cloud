package com.davies.naraka.puppeteer;

import org.springframework.data.domain.Pageable;

import javax.persistence.Query;
import java.util.function.UnaryOperator;

/**
 * @author davies
 * @date 2022/3/28 19:11
 */
public class QueryParams {


    private final String querySql;

    private final Pageable pageable;

    private final UnaryOperator<Query> queryConsumer;


    public QueryParams(String querySql, UnaryOperator<Query> queryConsumer, Pageable pageable) {
        this.querySql = querySql;
        this.queryConsumer = queryConsumer == null ? UnaryOperator.identity() : queryConsumer;
        this.pageable = pageable;
    }

    public QueryParams(String querySql, UnaryOperator<Query> queryConsumer) {
        this(querySql, queryConsumer, null);
    }

    public String getQuerySql() {
        return querySql;
    }


    public Pageable getPageable() {
        return pageable;
    }


    public UnaryOperator<Query> getQueryConsumer() {
        return queryConsumer;
    }


}
