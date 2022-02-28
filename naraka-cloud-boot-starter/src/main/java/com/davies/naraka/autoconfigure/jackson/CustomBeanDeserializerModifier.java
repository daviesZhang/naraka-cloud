package com.davies.naraka.autoconfigure.jackson;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * jackson反序列化时的自定义逻辑
 * 处理枚举反序列化,客户端使用的枚举均为code,当传入code时,根据toString方法比较枚举值并反序列化
 * 这要求所有带处理的枚举类必须重写toString 返回code
 *
 * @author davies
 * @date 2022/1/29 1:37 PM
 */
public class CustomBeanDeserializerModifier extends BeanDeserializerModifier {



    public CustomBeanDeserializerModifier() {

    }

    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        Class<?> enumClass = type.getRawClass();

        Optional<Field> fieldOptional = Arrays.stream(enumClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(EnumValue.class))
                .findFirst();
        if (fieldOptional.isPresent() && deserializer instanceof EnumDeserializer) {
            return new CustomEnumDeserializer((EnumDeserializer) deserializer, true);
        }

        return super.modifyEnumDeserializer(config, type, beanDesc, deserializer);
    }

    public static class CustomEnumDeserializer extends EnumDeserializer {


        protected CustomEnumDeserializer(EnumDeserializer base, Boolean caseInsensitive) {
            super(base, caseInsensitive);
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            Class<?> enumClass = handledType();
            if (enumClass.isEnum()) {
                Object value = p.getText();
                for (Object o : enumClass.getEnumConstants()) {
                    if (Objects.equals(o.toString(), value)) {
                        return o;
                    }
                }
                return null;
            }
            return super.deserialize(p, ctxt);
        }
    }


    //public static class QueryFilterDeserializer extends
}
