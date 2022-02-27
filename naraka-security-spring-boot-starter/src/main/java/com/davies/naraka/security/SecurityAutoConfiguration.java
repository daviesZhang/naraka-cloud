package com.davies.naraka.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Verification;
import com.davies.naraka.security.jackson.*;
import com.davies.naraka.security.properties.SecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateTimeKeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author davies
 * @date 2022/2/27 6:55 PM
 */
@Configuration
@ConditionalOnProperty(value = "naraka.security.enable", havingValue = "true")
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration {

    private final SecurityProperties securityProperties;

    public SecurityAutoConfiguration(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Bean
    @ConditionalOnClass(name = "javax.servlet.http.HttpServletRequest")
    @ConditionalOnMissingBean(CurrentUserNameSupplier.class)
    public CurrentUserNameSupplier currentUserNameSupplier() {
        return new RequestRemoteUserSupplier();
    }


    @Bean
    @ConditionalOnClass(name = {"org.redisson.api.RedissonClient"})
    @ConditionalOnBean({CurrentUserNameSupplier.class})
    @ConditionalOnMissingBean(ProcessorFunction.class)
    public ProcessorFunction processorFunction() {
        return new RedisProcessorFunction();
    }


    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.securityProperties.getDateTimePattern());
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.setSerializerModifier(customBeanSerializerModifier());
        javaTimeModule.setDeserializerModifier(new CustomBeanDeserializerModifier());
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addKeyDeserializer(LocalDateTime.class, LocalDateTimeKeyDeserializer.INSTANCE);
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        return objectMapper.registerModule(javaTimeModule)
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .setDateFormat(new SimpleDateFormat(this.securityProperties.getDateTimePattern()));
    }

    @Bean
    @ConditionalOnProperty(value = "naraka.security.jackson", havingValue = "true")
    public CustomBeanSerializerModifier customBeanSerializerModifier() {
        return new CustomBeanSerializerModifier(SerializeProcessorWrapper::getSerializeProcessor);
    }

    @Bean
    @ConditionalOnProperty(value = "naraka.security.jackson", havingValue = "true")
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.securityProperties.getDateTimePattern());
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        javaTimeModule.addKeyDeserializer(LocalDateTime.class, LocalDateTimeKeyDeserializer.INSTANCE);
        return objectMapper.registerModule(javaTimeModule)
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .setDateFormat(new SimpleDateFormat(this.securityProperties.getDateTimePattern()));
    }


    @Bean
    @ConditionalOnProperty(value = "naraka.security.jackson", havingValue = "true")
    @ConditionalOnMissingBean(MappingJacksonHttpMessageConverter.class)
    public MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter(
            CurrentUserNameSupplier currentUserNameSupplier
    ) {
        MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter(createObjectMapper());
        CacheObjectMapper cacheObjectMapper = new CacheObjectMapper(this::createObjectMapper, currentUserNameSupplier);
        cacheObjectMapper.setDefaultObjectMapper(objectMapper());
        converter.setObjectMapperSupplier(cacheObjectMapper);
        return converter;
    }


    @Bean
    @ConditionalOnMissingBean(Algorithm.class)
    public Algorithm algorithm() {
        return Algorithm.HMAC512(this.securityProperties.getJwtSecret());
    }

    @Bean
    @ConditionalOnMissingBean(Verification.class)
    public Verification jwtVerifier(Algorithm algorithm) {
        return JWT.require(algorithm);

    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 根据用户名构造一个jwt token
     *
     * @return BiFunction<用户名, 签发时间, token>
     */
    @Bean
    @ConditionalOnMissingBean
    public GeneratorTokenBiFunction generatorToken(Algorithm algorithm) {
        return (String username, Date issuedAt) -> JWT.create().withSubject(username)
                .withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(this.securityProperties.getExpiresAt())
                        .atZone(ZoneOffset.systemDefault()).toInstant()))
                .withIssuedAt(issuedAt)
                .withIssuer(username)
                .sign(algorithm);
    }
}
