package com.davies.naraka.admin.config.mybatis;

import com.google.common.base.Strings;
import com.davies.naraka.admin.annotation.Crypto;
import com.davies.naraka.admin.common.AesEncryptorUtils;
import com.davies.naraka.admin.config.properties.EncryptProperties;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @see com.davies.naraka.admin.annotation.Crypto
 * 查询结果解密
 * 不支持多层结构,仅支持 Object->field(解密) 或者List<Object->field(解密)>
 *     不支持Object->Object->field(解密)
 * @author davies
 * @date 2022/1/30 9:36 PM
 */
@Component
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class ResultCryptoInterceptor implements Interceptor {

    private final EncryptProperties encryptProperties;

    public ResultCryptoInterceptor(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        if (Objects.isNull(result)) {
            return null;
        }
        if (result instanceof List) {
            List<?> resultList = (List<?>) result;
            if (!resultList.isEmpty()) {
                for (Object o : resultList) {
                    Class<?> objectClass = o.getClass();
                    if(objectClass.getDeclaredAnnotation(Crypto.class)==null){
                        continue;
                    }
                    Field[] fields = objectClass.getDeclaredFields();
                    for (Field field:fields){
                        decrypt(field, o);
                    }
                }
            }
        } else {
            Class<?> objectClass = result.getClass();
            if(objectClass.getDeclaredAnnotation(Crypto.class)!=null){
                Field[] fields = objectClass.getDeclaredFields();
                for (Field field:fields){
                    decrypt(field, result);
                }
            }
        }
        return result;
    }

    private void decrypt(Field field,Object item) throws IllegalAccessException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Crypto crypto = field.getDeclaredAnnotation(Crypto.class);
        if (null==crypto){
            return;
        }
        field.setAccessible(true);
        String key = this.encryptProperties.getKey(Strings.isNullOrEmpty(crypto.name()) ? field.getName() : crypto.name());
        field.set(item, AesEncryptorUtils.decrypt((String)field.get(item),key));
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
