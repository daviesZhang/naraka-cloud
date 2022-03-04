package com.davies.naraka.autoconfigure.jackson;

import com.davies.naraka.cloud.common.StringUtils;

/**
 * 序列化脱敏
 *
 * @author davies
 * @date 2022/3/4 1:48 PM
 */
public class SerializeDesensitization implements SerializeBeanPropertyFunction {


    @Override
    public Object apply(Object o) {
        if (o instanceof String) {
            return StringUtils.desensitization((String) o);
        }
        return o;
    }
}
