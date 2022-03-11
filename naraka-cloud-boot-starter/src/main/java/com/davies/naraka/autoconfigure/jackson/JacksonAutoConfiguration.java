package com.davies.naraka.autoconfigure.jackson;

import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import com.davies.naraka.autoconfigure.ProcessorFunction;
import com.davies.naraka.autoconfigure.properties.JacksonProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author davies
 * @date 2022/3/4 1:45 PM
 */
@Configuration
@ConditionalOnClass(name = "com.fasterxml.jackson.databind.ObjectMapper")
@EnableConfigurationProperties(JacksonProperties.class)
public class JacksonAutoConfiguration {

    @Value("${naraka.jackson.deserializerSmart:true}")
    private Boolean smartConverter;

    @Value("${naraka.datetime.pattern:yyyy-MM-dd HH:mm:ss}")
    private String datetimePattern;


    @Bean
    public ObjectMapperFactory objectMapperFactory() {
        return new ObjectMapperFactory(this.smartConverter, this.datetimePattern);
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
    @ConditionalOnBean(value = {ProcessorFunction.class,CurrentUserNameSupplier.class})
    @ConditionalOnProperty(value = "naraka.jackson.serializerCustomField", havingValue = "true",matchIfMissing = true)
    public CustomFieldBeanSerializerModifier customBeanSerializerModifier(
            SerializeBeanPropertyFactory serializeBeanPropertyFactory,
            ProcessorFunction processorFunction,
            CurrentUserNameSupplier currentUserNameSupplier) {
        return new CustomFieldBeanSerializerModifier(serializeBeanPropertyFactory,
                processorFunction,
                currentUserNameSupplier);
    }

    @Bean
    @ConditionalOnProperty(value = "naraka.jackson.objectMapper", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper( ObjectMapperFactory objectMapperFactory,
            @Autowired(required = false) CustomFieldBeanSerializerModifier customFieldBeanSerializerModifier
    ) {
        return objectMapperFactory.apply(customFieldBeanSerializerModifier);
    }


    @Bean
    @ConditionalOnProperty(value = "naraka.jackson.messageConverter", havingValue = "true")
    @ConditionalOnMissingBean(MappingJacksonHttpMessageConverter.class)
    public MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter(
            ObjectMapperFactory objectMapperFactory,
            @Autowired(required = false) CurrentUserNameSupplier currentUserNameSupplier,
            @Autowired(required = false) CustomFieldBeanSerializerModifier customFieldBeanSerializerModifier
    ) {
        MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter(objectMapperFactory.apply(null));
        if (currentUserNameSupplier != null) {
            CacheObjectMapper cacheObjectMapper = new CacheObjectMapper(() -> objectMapperFactory.apply(customFieldBeanSerializerModifier), currentUserNameSupplier);
            converter.setObjectMapperSupplier(cacheObjectMapper);
        }
        return converter;
    }

}
