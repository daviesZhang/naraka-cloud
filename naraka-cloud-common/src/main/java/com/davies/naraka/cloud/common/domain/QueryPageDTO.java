package com.davies.naraka.cloud.common.domain;



import javax.validation.constraints.NotNull;

/**
 * @author davies
 * @date 2022/2/7 12:57 PM
 */

public class QueryPageDTO<T> {


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

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

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