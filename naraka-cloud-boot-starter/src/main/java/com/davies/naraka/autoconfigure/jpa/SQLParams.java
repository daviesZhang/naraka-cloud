package com.davies.naraka.autoconfigure.jpa;

import javax.persistence.Query;
import java.util.function.Function;

/**
 * @author davies
 * @date 2022/3/28 19:11
 */
public class SQLParams {


    protected final String sql;


    protected Function<Query, Query> queryConsumer;

    public SQLParams(String sql, Function<Query, Query> queryConsumer) {
        this.sql = sql;
        this.queryConsumer = queryConsumer;
    }


    public String getSql() {
        return sql;
    }

    public Function<Query, Query> getQueryConsumer() {
        return queryConsumer;
    }


    /**
     * 消费query
     *
     * @param function
     */
    public void queryConsumer(Function<Query, Query> function) {
        this.queryConsumer = this.queryConsumer.andThen(function);
    }
}
