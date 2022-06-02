package com.davies.naraka.autoconfigure;

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
    Integer getSize();

    /**
     * 当前页码
     *
     * @return
     */
    Integer getCurrent();

    /**
     * 得到查询条件
     *
     * @return
     */
    T getQuery();
}
