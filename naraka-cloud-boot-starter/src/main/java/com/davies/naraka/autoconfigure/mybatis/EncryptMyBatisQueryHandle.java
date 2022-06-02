package com.davies.naraka.autoconfigure.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.davies.naraka.autoconfigure.SupportEncryptHelper;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.autoconfigure.properties.EncryptProperties;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * mysql数据库的加密逻辑
 *
 * @author davies
 * @date 2022/5/4 18:48
 */
public class EncryptMyBatisQueryHandle implements MyBatisQueryHandle {


    private final EncryptProperties encryptProperties;

    public EncryptMyBatisQueryHandle(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }

    @Override
    public boolean support(QueryFilterType filterType, String column, Object value, Field field) {
        return SupportEncryptHelper.support(filterType, column, encryptProperties, field);
    }

    @Override
    public <T> QueryWrapper<T> handle(QueryWrapper<T> queryWrapper,
                                      QueryFilterType filterType,
                                      String column,
                                      Object value,
                                      Field field) {
        String key = SupportEncryptHelper.getEncryptKey(column, encryptProperties, field);
        switch (filterType) {
            case EQ:
                queryWrapper.eq(column, SupportEncryptHelper.encrypt(value, key));
                break;
            case NOT_EQ:
                queryWrapper.ne(column, SupportEncryptHelper.encrypt(value, key));
                break;
            case LIKE:
                queryWrapper.like(dbDecryptColumn(column, key), value);
                break;
            case STARTS_WITH:
                queryWrapper.likeRight(dbDecryptColumn(column, key), value);
                break;
            case ENDS_WITH:
                queryWrapper.likeLeft(dbDecryptColumn(column, key), value);
                break;
            case CONTAINS:
                this.queryListCondition(key, column, value, queryWrapper::in);
                break;
            case NOT_CONTAINS:
                this.queryListCondition(key, column, value, queryWrapper::notIn);
                break;
            default:
                throw new IllegalArgumentException(Strings.lenientFormat("%s 不支持这个运算符", filterType));
        }

        return queryWrapper;
    }


    private void queryListCondition(String key, String column, Object value, BiConsumer<String, List<?>> biFunction) {
        if (value instanceof List) {
            List<String> values = ((List<?>) value)
                    .stream()
                    .map(v -> SupportEncryptHelper.encrypt(v, key))
                    .collect(Collectors.toList());
            if (!values.isEmpty()) {
                biFunction.accept(column, values);
            }
        } else {
            biFunction.accept(column, Lists.newArrayList(SupportEncryptHelper.encrypt(value, key)));
        }

    }

    private String dbDecryptColumn(String column, String key) {
        return "AES_DECRYPT(UNHEX(" + column + "),'" + key + "')";
    }


}

