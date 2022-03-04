package com.davies.naraka.autoconfigure.mybatis;

/**
 * @author davies
 * @date 2022/3/4 9:15 PM
 */
public interface QueryPage<T> {

    /**
     * 当前页码大小
     *
     * @return
     */
    Long getSize();

    /**
     * 当前页码
     *
     * @return
     */
    Long getCurrent();

    /**
     * 得到查询条件
     *
     * @return
     */
    T getQuery();
}
