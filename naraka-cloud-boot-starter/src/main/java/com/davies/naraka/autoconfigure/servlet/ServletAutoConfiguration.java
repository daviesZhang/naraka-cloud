package com.davies.naraka.autoconfigure.servlet;

import com.davies.naraka.autoconfigure.*;
import com.davies.naraka.autoconfigure.jackson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author davies
 * @date 2022/2/28 3:19 PM
 */
@Configuration
@ConditionalOnClass(name = "javax.servlet.http.HttpServletRequest")
public class ServletAutoConfiguration {

    @Value("${naraka.datetime.pattern:'yyyy-MM-dd HH:mm:ss'}")
    private String datetimePattern;




    @Bean
    @ConditionalOnProperty(name = "naraka.username.header")
    @ConditionalOnMissingBean(RemoteUserFilter.class)
    public RemoteUserFilter remoteUserFilter(){
        return new RemoteUserFilter();
    }


    @Bean
    @ConditionalOnMissingBean(CurrentUserNameSupplier.class)
    public CurrentUserNameSupplier currentUserNameSupplier() {
        return new RequestRemoteUserSupplier();
    }

    // 10点

    private ObjectMapper createObjectMapper(CustomBeanSerializerModifier customBeanSerializerModifier) {
        ObjectMapper objectMapper = new ObjectMapper();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.datetimePattern);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.setSerializerModifier(customBeanSerializerModifier);
        javaTimeModule.setDeserializerModifier(new CustomBeanDeserializerModifier());
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addKeyDeserializer(LocalDateTime.class, LocalDateTimeKeyDeserializer.INSTANCE);
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        return objectMapper.registerModule(javaTimeModule)
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .setDateFormat(new SimpleDateFormat(this.datetimePattern));
    }

    @Bean
    @ConditionalOnClass(name = {"org.redisson.api.RedissonClient"})
    @ConditionalOnBean({CurrentUserNameSupplier.class})
    @ConditionalOnMissingBean(ProcessorFunction.class)
    public ProcessorFunction processorFunction() {
        return new RedisProcessorFunction();
    }

    @Bean
    @ConditionalOnProperty(value = "naraka.jackson", havingValue = "true")
    public CustomBeanSerializerModifier customBeanSerializerModifier(ProcessorFunction processorFunction,CurrentUserNameSupplier currentUserNameSupplier) {
        return new CustomBeanSerializerModifier(SerializeProcessorWrapper::getSerializeProcessor, processorFunction, currentUserNameSupplier);
    }

    @Bean
    @ConditionalOnProperty(value = "naraka.jackson", havingValue = "true")
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.datetimePattern);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        javaTimeModule.addKeyDeserializer(LocalDateTime.class, LocalDateTimeKeyDeserializer.INSTANCE);
        return objectMapper.registerModule(javaTimeModule)
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .setDateFormat(new SimpleDateFormat(this.datetimePattern));
    }


    @Bean
    @ConditionalOnProperty(value = "naraka.jackson", havingValue = "true")
    @ConditionalOnMissingBean(MappingJacksonHttpMessageConverter.class)
    public MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter(
            CurrentUserNameSupplier currentUserNameSupplier,
            CustomBeanSerializerModifier customBeanSerializerModifier
    ) {
        MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter(createObjectMapper(customBeanSerializerModifier));
        CacheObjectMapper cacheObjectMapper = new CacheObjectMapper(()->createObjectMapper(customBeanSerializerModifier), currentUserNameSupplier);
        cacheObjectMapper.setDefaultObjectMapper(objectMapper());
        converter.setObjectMapperSupplier(cacheObjectMapper);
        return converter;
    }
}
