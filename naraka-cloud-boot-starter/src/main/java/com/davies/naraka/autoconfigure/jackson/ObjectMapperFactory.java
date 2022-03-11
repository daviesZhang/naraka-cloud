package com.davies.naraka.autoconfigure.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * @author davies
 */
public class ObjectMapperFactory implements Function<CustomFieldBeanSerializerModifier, ObjectMapper> {


    private final Boolean smartConverter;


    private final String datetimePattern;

    public ObjectMapperFactory(Boolean smartConverter, String datetimePattern) {
        this.smartConverter = smartConverter;
        this.datetimePattern = datetimePattern;
    }


    @Override
    public ObjectMapper apply(CustomFieldBeanSerializerModifier customFieldBeanSerializerModifier) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        if (this.smartConverter) {
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.datetimePattern);
        JavaTimeModule defaultModule = new JavaTimeModule();
        if (customFieldBeanSerializerModifier != null) {
            defaultModule.setSerializerModifier(customFieldBeanSerializerModifier);
        }
        defaultModule.setDeserializerModifier(new CustomBeanDeserializerModifier(this.smartConverter));
        defaultModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        defaultModule.addKeyDeserializer(LocalDateTime.class, LocalDateTimeKeyDeserializer.INSTANCE);
        defaultModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        return objectMapper.registerModule(defaultModule)
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .setDateFormat(new SimpleDateFormat(this.datetimePattern));
    }
}
