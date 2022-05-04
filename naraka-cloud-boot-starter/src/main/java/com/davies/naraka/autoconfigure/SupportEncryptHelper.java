package com.davies.naraka.autoconfigure;

import com.davies.naraka.autoconfigure.annotation.Crypto;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.autoconfigure.properties.EncryptProperties;
import com.davies.naraka.cloud.common.AesEncryptorUtils;
import com.davies.naraka.cloud.common.StringConstants;
import com.google.common.base.Strings;
import org.jetbrains.annotations.Nullable;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author davies
 * @date 2022/5/4 20:10
 */
public class SupportEncryptHelper {


    public static boolean support(QueryFilterType filterType, String column, @Nullable EncryptProperties encryptProperties, @Nullable Field field) {
        switch (filterType) {
            case ORDER_ASC:
            case ORDER_DESC:
            case LESSTHANEQUAL:
            case LESSTHAN:
            case GREATERTHANE:
            case GREATERTHANEQUAL:
                return false;
            default:
                String key = getEncryptKey(column, encryptProperties, field);
                return !Strings.isNullOrEmpty(key);
        }
    }


    public static String encrypt(Object object, String key) {
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

    public static String getEncryptKey(String column, @Nullable EncryptProperties encryptProperties, @Nullable Field field) {
        if (encryptProperties == null || !encryptProperties.isEnable()) {
            return null;
        }
        String name = column;
        if (field != null) {
            Crypto crypto = field.getDeclaredAnnotation(Crypto.class);
            if (crypto != null && !Strings.isNullOrEmpty(crypto.value())) {
                name = crypto.value();
            }
        }
        int dotIndex = name.indexOf(StringConstants.DOT);
        if (dotIndex >= 0) {
            return encryptProperties.getKey(name.substring(dotIndex));
        }
        return encryptProperties.getKey(name);


    }
}
