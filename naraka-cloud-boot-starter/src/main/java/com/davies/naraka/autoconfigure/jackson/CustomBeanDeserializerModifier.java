package com.davies.naraka.autoconfigure.jackson;

import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * jackson反序列化时的自定义逻辑
 *
 * @author davies
 * @date 2022/1/29 1:37 PM
 */
public class CustomBeanDeserializerModifier extends BeanDeserializerModifier {

    private final boolean smartQueryParams;

    public CustomBeanDeserializerModifier(boolean smartQueryParams) {
        this.smartQueryParams = smartQueryParams;
    }


    @Override
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        if (smartQueryParams && QueryField.class.isAssignableFrom(beanDesc.getBeanClass())) {
            BeanDeserializer beanDeserializer = (BeanDeserializer) deserializer;
            return new CustomQueryFieldDeserializer(beanDeserializer, beanDesc);
        }

        return super.modifyDeserializer(config, beanDesc, deserializer);
    }


    /**
     *  处理枚举反序列化,客户端使用的枚举均为code,当传入code时,根据toString方法比较枚举值并反序列化
     *  这要求所有带处理的枚举类必须重写toString 返回code
     * @param config
     * @param type
     * @param beanDesc
     * @param deserializer
     * @return
     */
    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        Class<?> enumClass = type.getRawClass();

        Optional<Field> fieldOptional = Arrays.stream(enumClass.getDeclaredFields())

                .filter(field -> field.isAnnotationPresent(JsonValue.class))
                .findFirst();
        if (fieldOptional.isPresent() && deserializer instanceof EnumDeserializer) {
            return new CustomEnumDeserializer((EnumDeserializer) deserializer, true);
        }

        return super.modifyEnumDeserializer(config, type, beanDesc, deserializer);
    }

    public static class CustomEnumDeserializer extends EnumDeserializer {


        private static final long serialVersionUID = -996148722305345048L;

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


    public static class CustomQueryFieldDeserializer extends BeanDeserializer {


        private static final long serialVersionUID = -6391485381305918252L;
        BeanDescription beanDesc;
        BeanDeserializerBase src;

        private static final String QUERY_FIELD_FILTER = "filter";


        protected CustomQueryFieldDeserializer(BeanDeserializerBase src, BeanDescription beanDesc) {
            super(src);
            this.src = src;
            this.beanDesc = beanDesc;
        }

        /**
         * 如果是对象,直接尝试转换为QueryField
         * 如果是数组,转换为数组,采用CONTAINS 查询
         * 其他类型采用 EQUALS
         * @param p
         * @param ctxt
         * @return
         * @throws IOException
         */
        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {


            if (p.currentToken() == JsonToken.START_OBJECT) {
                return this.src.deserialize(p, ctxt);
            }
            if (p.currentToken() == JsonToken.START_ARRAY) {
                Object value = this.src.findProperty(QUERY_FIELD_FILTER).getValueDeserializer().deserialize(p, ctxt);
                return new QueryField<Object>(QueryFilterType.CONTAINS, value);
            }
            Object value = this.src.findProperty(QUERY_FIELD_FILTER).getValueDeserializer().deserialize(p, ctxt);
            return new QueryField<Object>(QueryFilterType.EQUALS, value);
        }


    }
}
