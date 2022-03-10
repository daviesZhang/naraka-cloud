package com.davies.naraka.autoconfigure.jackson;


import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import com.davies.naraka.autoconfigure.ProcessorFunction;
import com.davies.naraka.autoconfigure.security.SecurityHelper;
import com.davies.naraka.cloud.common.StringConstants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author davies
 * @date 2022/1/26 12:32 PM
 */

public class CustomBeanSerializerModifier extends BeanSerializerModifier {


    @Autowired
    private HttpServletRequest request;


    private final ProcessorFunction processorFunction;


    private final CurrentUserNameSupplier currentUserNameSupplier;


    private final SerializeBeanPropertyFactory serializeBeanPropertyFactory;

    public CustomBeanSerializerModifier(SerializeBeanPropertyFactory serializeBeanPropertyFactory,
                                        ProcessorFunction processorFunction,
                                        CurrentUserNameSupplier currentUserNameSupplier) {
        this.serializeBeanPropertyFactory = serializeBeanPropertyFactory;
        this.processorFunction = processorFunction;
        this.currentUserNameSupplier = currentUserNameSupplier;
    }

    /**
     * 已登录用户,根据配置的权限,对待序列化的字段进行处理 AuthorityProcessorType
     * 一个字段允许多种处理策略
     *
     * @param config
     * @param beanDesc
     * @param beanProperties
     * @return
     */
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {

        String username = currentUserNameSupplier.get();
        if (Strings.isNullOrEmpty(username)) {
            return beanProperties;
        }
        if (SecurityHelper.isRoot(username)) {
            return beanProperties;
        }
        if (processorFunction==null){
            return beanProperties;
        }
        String authorityKey = request.getMethod().toLowerCase() + StringConstants.SPACE + request.getRequestURI();

        Map<String, Set<String>> valueAuthorityMap = processorFunction.apply(authorityKey);

        if (valueAuthorityMap.isEmpty()) {
            return beanProperties;
        }
        List<BeanPropertyWriter> propertyWriterList = Lists.newArrayList();
        for (final BeanPropertyWriter writer : beanProperties) {
            String field = writer.getName();
            Set<String> list = valueAuthorityMap.get(field);
            if (list == null || list.isEmpty()) {
                propertyWriterList.add(writer);
                continue;
            }
            SerializeProcessor serializeProcessorWrapper = getSerializeProcessor(list);
            if (serializeProcessorWrapper == null) {
                continue;
            }
            propertyWriterList.add(new ProcessorBeanPropertyWriter(writer, serializeProcessorWrapper));
        }
        return propertyWriterList;
    }


    private SerializeProcessor getSerializeProcessor(Set<String> authorityProcessorTypes) {
        SerializeProcessor wrapper = null;
        for (String processor : authorityProcessorTypes) {
            //过滤策略直接跳过序列化动作
            if (SerializeBeanPropertyFactory.SERIALIZE_SKIP.equals(processor)) {
                return null;
            }
            SerializeBeanPropertyFunction function = serializeBeanPropertyFactory.apply(processor);
            wrapper = new SerializeProcessor(function, wrapper);
        }
        return wrapper;
    }


    private static class ProcessorBeanPropertyWriter extends BeanPropertyWriter {
        private static final long serialVersionUID = -4632447338147399273L;
        private final SerializeProcessor processor;

        protected ProcessorBeanPropertyWriter(BeanPropertyWriter base, SerializeProcessor processor) {
            super(base);
            this.processor = processor;
        }

        /**
         * 拷贝了父类方法
         */
        private void serializeAsField(Object bean, JsonGenerator gen,
                                      SerializerProvider prov, Object value) throws Exception {
            // Null handling is bit different, check that first
            if (value == null) {
                if (_nullSerializer != null) {
                    gen.writeFieldName(_name);
                    _nullSerializer.serialize(null, gen, prov);
                }
                return;
            }
            // then find serializer to use
            JsonSerializer<Object> ser = _serializer;
            if (ser == null) {
                Class<?> cls = value.getClass();
                PropertySerializerMap m = _dynamicSerializers;
                ser = m.serializerFor(cls);
                if (ser == null) {
                    ser = _findAndAddDynamic(m, cls, prov);
                }
            }
            // and then see if we must suppress certain values (default, empty)
            if (_suppressableValue != null) {
                if (MARKER_FOR_EMPTY == _suppressableValue) {
                    if (ser.isEmpty(prov, value)) {
                        return;
                    }
                } else if (_suppressableValue.equals(value)) {
                    return;
                }
            }
            // For non-nulls: simple check for direct cycles
            if (value == bean) {
                // four choices: exception; handled by call; pass-through or write null
                if (_handleSelfReference(bean, gen, prov, ser)) {
                    return;
                }
            }
            gen.writeFieldName(_name);
            if (_typeSerializer == null) {
                ser.serialize(value, gen, prov);
            } else {
                ser.serializeWithType(value, gen, prov, _typeSerializer);
            }
        }


        @Override
        public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
            final Object value = (_accessorMethod == null) ? _field.get(bean) : _accessorMethod.invoke(bean, (Object[]) null);

            serializeAsField(bean, gen, prov, this.processor.apply(value));

        }


    }


}
