package com.davies.naraka.autoconfigure;

import com.davies.naraka.autoconfigure.annotation.ColumnName;
import com.davies.naraka.autoconfigure.annotation.Crypto;
import com.davies.naraka.autoconfigure.annotation.QuerySkip;
import com.davies.naraka.autoconfigure.properties.EncryptProperties;
import com.davies.naraka.cloud.common.AesEncryptorUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.function.BiFunction;



@Slf4j
public abstract class QueryUtils {


    private final EncryptProperties encryptProperties;


    public QueryUtils() {
        this.encryptProperties = null;
    }

    public QueryUtils(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }


    protected String getEncryptKey(Field field) {
        if (encryptProperties == null || !encryptProperties.isEnable()) {
            return null;
        }
        Crypto crypto = field.getDeclaredAnnotation(Crypto.class);
        String key = null;
        if (crypto != null) {
            key = encryptProperties.getKey(Strings.isNullOrEmpty(crypto.name()) ? field.getName() : crypto.name());
        }
        return key;
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
        if (columnName != null && !Strings.isNullOrEmpty(columnName.name())) {
            column = columnName.name();
        }
        //String key = getEncryptKey(field);
        return Optional.ofNullable(function.apply(column, value));

    }


}
