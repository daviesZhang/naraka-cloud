package com.davies.naraka.autoconfigure.mybatis;

import com.davies.naraka.autoconfigure.properties.EncryptProperties;
import com.davies.naraka.cloud.common.AesEncryptorUtils;
import com.davies.naraka.cloud.common.annotation.Crypto;
import com.google.common.base.Strings;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Properties;

/**
 * @see com.davies.naraka.admin.annotation.Crypto
 * @author davies
 * @date 2022/1/30 8:55 PM
 */

@Intercepts(value =
        {@Signature(type = ParameterHandler.class// 确定要拦截的对象
                , method = "setParameters", args = PreparedStatement.class// 拦截方法的参数
        )})
public class ParamsCryptoInterceptor implements Interceptor {


    private final EncryptProperties encryptProperties;

    public ParamsCryptoInterceptor(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();
        Field parameterField = parameterHandler.getClass().getDeclaredField("parameterObject");
        parameterField.setAccessible(true);
        Object parameterObject = parameterHandler.getParameterObject();
        if (parameterObject != null) {
            if (parameterObject instanceof List) {
                List<Object> list = (List) parameterObject;
                if (!list.isEmpty()) {
                    for (Object item : list) {
                        Class<?> objectClass = item.getClass();
                        Crypto crypto = objectClass.getDeclaredAnnotation(Crypto.class);
                        if (null == crypto) {
                            continue;
                        }
                        Field[] fields = objectClass.getDeclaredFields();
                        for (Field field : fields) {
                            encrypt(field, item);
                        }
                    }
                }
            } else {
                Class<?> objectClass = parameterObject.getClass();
                Crypto crypto = objectClass.getDeclaredAnnotation(Crypto.class);
                if (null != crypto) {
                    Field[] fields = objectClass.getDeclaredFields();
                    for (Field field : fields) {
                        encrypt(field, parameterObject);
                    }
                }
            }
        }
        return invocation.proceed();

        //return null;
    }

    private void encrypt(Field field, Object item) throws IllegalAccessException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Crypto fieldCrypto = field.getDeclaredAnnotation(Crypto.class);
        if (fieldCrypto == null) {
            return;
        }
        field.setAccessible(true);
        String key = this.encryptProperties.getKey(Strings.isNullOrEmpty(fieldCrypto.name()) ? field.getName() : fieldCrypto.name());
        field.set(item, AesEncryptorUtils.encrypt((String) field.get(item), key));
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
