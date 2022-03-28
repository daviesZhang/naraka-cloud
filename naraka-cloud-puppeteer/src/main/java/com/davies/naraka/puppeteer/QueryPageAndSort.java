package com.davies.naraka.puppeteer;

import com.davies.naraka.autoconfigure.QueryPage;

/**
 * 分页查询和排序
 *
 * @author davies
 * @date 2022/3/28 22:29
 */
public interface QueryPageAndSort<T> extends QueryPage<T>, QueryAndSort<T> {


}