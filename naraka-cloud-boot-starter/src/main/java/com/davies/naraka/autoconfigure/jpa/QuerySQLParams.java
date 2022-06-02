package com.davies.naraka.autoconfigure.jpa;

import org.springframework.data.domain.Pageable;

import javax.persistence.Query;
import java.util.function.Function;

/**
 * @author davies
 * @date 2022/3/28 19:11
 */
public class QuerySQLParams extends SQLParams {


    private Pageable pageable;

    public QuerySQLParams(String sql, Function<Query, Query> queryConsumer) {
        super(sql, queryConsumer);
    }

    public QuerySQLParams(String sql, Function<Query, Query> queryConsumer, Pageable pageable) {
        super(sql, queryConsumer);
        this.pageable = pageable;
    }

    public Pageable getPageable() {
        return pageable;
    }

}
