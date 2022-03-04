package com.davies.naraka.autoconfigure.jackson;

import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import com.davies.naraka.autoconfigure.ProcessorFunction;
import com.davies.naraka.autoconfigure.RedisProcessorFunction;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author davies
 * @date 2022/3/4 1:45 PM
 */
@Configuration
@ConditionalOnProperty(value = "naraka.jackson.objectMapper", havingValue = "true")
@ConditionalOnClass(name = "com.fasterxml.jackson.databind.ObjectMapper")
public class JacksonAutoConfiguration {

    @Value("${naraka,jackson.smartConverter:true}")
    private Boolean smartConverter;

    @Value("${naraka.datetime.pattern:yyyy-MM-dd HH:mm:ss}")
    private String datetimePattern;


    /**
     * 当值为null时不序列化
     * 当要求为数组时,允许传入单个对象
     *
     * @param customBeanSerializerModifier
     * @return
     */
    private ObjectMapper createObjectMapper(CustomBeanSerializerModifier customBeanSerializerModifier) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        if (this.smartConverter) {
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.datetimePattern);
        JavaTimeModule defaultModule = new JavaTimeModule();
        if (customBeanSerializerModifier != null) {
            defaultModule.setSerializerModifier(customBeanSerializerModifier);
        }
        defaultModule.setDeserializerModifier(new CustomBeanDeserializerModifier(this.smartConverter));
        defaultModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        defaultModule.addKeyDeserializer(LocalDateTime.class, LocalDateTimeKeyDeserializer.INSTANCE);
        defaultModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        return objectMapper.registerModule(defaultModule)
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .setDateFormat(new SimpleDateFormat(this.datetimePattern));
    }


    @Bean
    @ConditionalOnClass(name = {"org.redisson.api.RedissonClient"})
    @ConditionalOnMissingBean(ProcessorFunction.class)
    public ProcessorFunction processorFunction(CurrentUserNameSupplier currentUserNameSupplier, RedissonClient redissonClient) {
        return new RedisProcessorFunction(currentUserNameSupplier, redissonClient);
    }


    @Bean
    @ConditionalOnMissingBean(name = "serializeDesensitization")
    public SerializeDesensitization serializeDesensitization() {
        return new SerializeDesensitization();
    }

    @Bean
    @ConditionalOnMissingBean(SerializeBeanPropertyFactory.class)
    public SerializeBeanPropertyBeanFactory serializeBeanPropertyBeanFactory(ApplicationContext applicationContext) {
        return new SerializeBeanPropertyBeanFactory(applicationContext);
    }


    @Bean
    @ConditionalOnProperty(value = "naraka.jackson.messageConverter", havingValue = "true")
    public CustomBeanSerializerModifier customBeanSerializerModifier(
            SerializeBeanPropertyFactory serializeBeanPropertyFactory,
            ProcessorFunction processorFunction, CurrentUserNameSupplier currentUserNameSupplier) {
        return new CustomBeanSerializerModifier(serializeBeanPropertyFactory, processorFunction, currentUserNameSupplier);
    }

    @Bean
    @ConditionalOnProperty(value = "naraka.jackson.messageConverter", havingValue = "true")
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper(CustomBeanSerializerModifier customBeanSerializerModifier) {
        return createObjectMapper(customBeanSerializerModifier);
    }


    @Bean
    @ConditionalOnProperty(value = "naraka.jackson.messageConverter", havingValue = "true")
    @ConditionalOnMissingBean(MappingJacksonHttpMessageConverter.class)
    public MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter(
            CurrentUserNameSupplier currentUserNameSupplier,
            CustomBeanSerializerModifier customBeanSerializerModifier
    ) {
        MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter(createObjectMapper(customBeanSerializerModifier));
        CacheObjectMapper cacheObjectMapper = new CacheObjectMapper(() -> createObjectMapper(customBeanSerializerModifier), currentUserNameSupplier);
        converter.setObjectMapperSupplier(cacheObjectMapper);
        return converter;
    }

}
