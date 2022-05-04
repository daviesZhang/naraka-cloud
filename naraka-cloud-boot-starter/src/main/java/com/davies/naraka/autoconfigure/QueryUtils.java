package com.davies.naraka.autoconfigure;

import com.davies.naraka.autoconfigure.annotation.ColumnName;
import com.davies.naraka.autoconfigure.annotation.QueryFilter;
import com.davies.naraka.autoconfigure.annotation.QuerySkip;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.cloud.common.AesEncryptorUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;


@Slf4j
public abstract class QueryUtils {


    protected void checkFilterType(QueryField<?> queryField, Field field) {
        QueryFilter queryFilter = field.getAnnotation(QueryFilter.class);
        if (queryFilter != null) {
            QueryFilterType[] types = queryFilter.types();
            boolean supportType = Arrays.stream(types).anyMatch(type -> type == queryField.getType());
            Preconditions.checkArgument(supportType, "filterType not support");
        }
    }


    protected String encrypt(Object object, String key) {
        try {
            if (object instanceof String) {
                return AesEncryptorUtils.encrypt((String) object, key);
            }
            throw new IllegalArgumentException("加密异常,值必须为String类型");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException("加密异常", e);
        }

    }


    protected Optional<Object> queryValue(Object object, Field field) {
        if (field.isAnnotationPresent(QuerySkip.class)) {
            return Optional.empty();
        }
        return ClassUtils.getFieldValueAndIgnoreError(object, field);

    }

    protected <T> Optional<T> fieldPredicate(Field field, Object object, BiFunction<String, Object, T> function) {
        if (field.isAnnotationPresent(QuerySkip.class)) {
            return Optional.empty();
        }
        Optional<Object> valueOptional = ClassUtils.getFieldValueAndIgnoreError(object, field);
        if (!valueOptional.isPresent()) {
            return Optional.empty();
        }
        Object value = valueOptional.get();
        String column = field.getName();
        ColumnName columnName = field.getDeclaredAnnotation(ColumnName.class);
        if (columnName != null && !Strings.isNullOrEmpty(columnName.value())) {
            column = columnName.value();
        }
        //String key = getEncryptKey(field);
        return Optional.ofNullable(function.apply(column, value));

    }


}
