package com.davies.naraka.autoconfigure.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author davies
 * @date 2022/5/4 18:45
 */
class DefaultMyBatisQueryHandle implements MyBatisQueryHandle {


    @Override
    public boolean support(QueryFilterType filterType, String column, Object value, Field field) {
        return true;
    }


    @Override
    public <T> QueryWrapper<T> handle(QueryWrapper<T> queryWrapper, QueryFilterType filterType, String column, Object value, Field field) {
        switch (filterType) {
            case EQ:
                queryWrapper.eq(column, value);
                break;
            case NOT_EQ:
                queryWrapper.ne(column, value);
                break;
            case LIKE:
                queryWrapper.like(column, value);
                break;
            case STARTS_WITH:
                queryWrapper.likeRight(column, value);
                break;
            case ENDS_WITH:
                queryWrapper.likeLeft(column, value);
                break;
            case CONTAINS:
                this.queryListCondition(column, value, queryWrapper::in);
                break;
            case NOT_CONTAINS:
                this.queryListCondition(column, value, queryWrapper::notIn);
                break;
            case LT:
                queryWrapper.lt(column, value);
                break;
            case GT:
                queryWrapper.gt(column, value);
                break;
            case LE:
                queryWrapper.le(column, value);
                break;
            case GE:
                queryWrapper.ge(column, value);
                break;
            case ASC:
                queryWrapper.orderByAsc(column);
                break;
            case DESC:
                queryWrapper.orderByDesc(column);
                break;
            default:
                throw new IllegalArgumentException(Strings.lenientFormat("%s 不支持这个运算符", filterType));
        }
        return queryWrapper;
    }

    private void queryListCondition(String column, Object value, BiConsumer<String, List<?>> biFunction) {
        if (value instanceof Collection) {
            Collection<?> values = (Collection<?>) value;
            if (!values.isEmpty()) {
                biFunction.accept(column, Lists.newArrayList(values));
            }
        } else {
            biFunction.accept(column, Lists.newArrayList(value));
        }
    }
}
