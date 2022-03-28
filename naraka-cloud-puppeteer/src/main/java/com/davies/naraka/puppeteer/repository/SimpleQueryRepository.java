package com.davies.naraka.puppeteer.repository;

import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;

/**
 * @author davies
 * @date 2022/3/28 18:45
 */
public interface SimpleQueryRepository {


    /**
     * @param <T>         结果包装类
     * @param <V>         最终输出类型
     * @param queryParams 查询参数
     * @param from        查询HQL语句 例如 select c from User或者 select c,g from User left join Group g on c.id=g.id
     * @param pageable    分页信息
     * @param tClass      结果包装类
     * @param convert     类型转换器
     * @return Page
     */
    <T, V> Page<V> page(@Nullable Object queryParams, @Nullable String from, @Nullable Pageable pageable,
                        Class<T> tClass, Function<T, V> convert);


    /**
     * @param queryParams 查询参数
     * @param from        查询HQL语句 例如 select c from User或者 select c,g from User left join Group g on c.id=g.id
     * @param sort        排序参数
     * @param tClass      结果包装类
     * @param convert     类型转换器
     * @param <T>         结果包装类
     * @param <V>         最终输出类型
     * @return List
     */
    <T, V> @Nullable List<V> list(@Nullable Object queryParams, @Nullable String from, Sort sort, Class<T> tClass, Function<T, V> convert);


    default <V> List<V> list(Object queryParams, String from) {
        return this.list(queryParams, from, null, null, null);
    }


    default <V> List<V> list(Object queryParams, String from, Function<?, V> convert) {
        return this.list(queryParams, from, null, null, convert);
    }


    default <V> List<V> list(Object queryParams, String from, Sort sort, Function<?, V> convert) {
        return this.list(queryParams, from, sort, null, convert);
    }

    default <T> List<T> list(Object queryParams, String from, Sort sort, Class<T> tClass) {
        return this.list(queryParams, from, sort, tClass, null);
    }

    default <T> List<T> list(Object queryParams, String from, Class<T> tClass) {
        return this.list(queryParams, from, null, tClass, null);
    }

    default <V> Page<V> page(Object queryParams, Pageable pageable, String from) {
        return this.page(queryParams, from, pageable, null, null);
    }

    default <V> Page<V> page(Object queryParams, Pageable pageable, String from, Function<?, V> convert) {
        return this.page(queryParams, from, pageable, null, convert);
    }


    default <T> Page<T> page(Object queryParams, Pageable pageable, String from, Class<T> tClass) {
        return this.page(queryParams, from, pageable, tClass, null);
    }
}
