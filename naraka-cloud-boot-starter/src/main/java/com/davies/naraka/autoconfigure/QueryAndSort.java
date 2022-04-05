package com.davies.naraka.autoconfigure;

import java.util.List;

/**
 * 查询和排序
 *
 * @author davies
 * @date 2022/3/28 22:31
 */
public interface QueryAndSort<T> {
    /**
     * 查询参数
     *
     * @return
     */
    T getQuery();

    /**
     * desc 排序的属性列表
     *
     * @return
     */
    List<String> getDesc();

    /**
     * asc 排序的属性列表
     *
     * @return
     */
    List<String> getAsc();
}
