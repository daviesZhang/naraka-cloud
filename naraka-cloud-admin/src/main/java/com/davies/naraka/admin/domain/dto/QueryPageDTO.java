package com.davies.naraka.admin.domain.dto;


import com.davies.naraka.autoconfigure.mybatis.QueryPage;

import javax.validation.constraints.NotNull;

/**
 * @author davies
 * @date 2022/2/7 12:57 PM
 */

public class QueryPageDTO<T> implements QueryPage<T> {


    /**
     * 每页显示条数，默认 10
     */
    @NotNull
    private Long size;

    /**
     * 当前页
     */
    @NotNull
    private Long current;

    /**
     * 查询条件
     */
    @NotNull
    private T query;

    @Override
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    @Override
    public T getQuery() {
        return query;
    }

    public void setQuery(T query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "QueryPageDTO{" +
                "size=" + size +
                ", current=" + current +
                ", query=" + query +
                '}';
    }
}
