package com.davies.naraka.system.controller;

import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.jpa.QuerySQLParams;
import com.davies.naraka.autoconfigure.jpa.SQLParamsProvider;
import com.davies.naraka.system.QueryLogicDeleteInterceptor;
import org.springframework.data.domain.Page;

/**
 * @author davies
 * @date 2022/6/1 08:42
 */
public class BaseController {


    /**
     * @param page
     * @param <T>
     * @return
     */
    protected <T> PageDTO<T> transform(Page<T> page) {
        return new PageDTO<>(page.getNumber() + 1,
                (int) page.getTotalElements(),
                page.getSize(),
                page.getContent());
    }

    /**
     * @param query
     * @return
     */
    protected QuerySQLParams queryParams(Object query) {
        return this.queryParams(query, null);
    }

    /**
     * 附加逻辑删字段过滤的拦截器
     * 始终在查询条件添加  logicDelete =:logicDelete
     *
     * @param query
     * @param logicDelete
     * @return
     */
    protected QuerySQLParams queryParams(Object query, String logicDelete) {
        return SQLParamsProvider.query(query, new QueryLogicDeleteInterceptor<>(logicDelete));
    }


}
