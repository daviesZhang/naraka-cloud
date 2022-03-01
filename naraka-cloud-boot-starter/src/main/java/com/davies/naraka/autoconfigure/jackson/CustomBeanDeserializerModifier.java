package com.davies.naraka.autoconfigure.jackson;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.davies.naraka.cloud.common.domain.QueryField;
import com.davies.naraka.cloud.common.enums.QueryFilterType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Strings;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
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

    private final boolean smartQueryParams;

    public CustomBeanDeserializerModifier(boolean smartQueryParams) {
        this.smartQueryParams = smartQueryParams;
    }


    @Override
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        if (smartQueryParams && beanDesc.getBeanClass().isAssignableFrom(QueryField.class)) {
            BeanDeserializer beanDeserializer = (BeanDeserializer) deserializer;
            return new CustomQueryFieldDeserializer(beanDeserializer, beanDesc);
        }

        return super.modifyDeserializer(config, beanDesc, deserializer);
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
