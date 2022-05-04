package com.davies.naraka.autoconfigure.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * @author davies
 * @date 2022/5/4 18:36
 */
public interface MyBatisQueryHandle {

    /**
     * @param column 查询的字段名
     * @param value  查询的值
     * @param field  查询的类字段,非类查询可能为空,比如map
     * @return true支持处理,
     */
    boolean support(QueryFilterType filterType, String column, Object value, @Nullable Field field);


    /**
     * @param queryWrapper
     * @param column
     * @param value
     * @param field
     * @return
     */
    <T> QueryWrapper<T> handle(QueryWrapper<T> queryWrapper, QueryFilterType filterType, String column, Object value, @Nullable Field field);
}
