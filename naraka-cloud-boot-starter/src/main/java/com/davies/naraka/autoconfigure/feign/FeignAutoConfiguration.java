package com.davies.naraka.autoconfigure.feign;

import com.davies.naraka.autoconfigure.GeneratorTokenBiFunction;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import static com.davies.naraka.cloud.common.StringConstants.SERVICE_TOKEN_ISSUER;

/**
 * @author davies
 * @date 2022/3/20 13:19
 */
@Configuration
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class FeignAutoConfiguration {


    @Bean
    RequestInterceptor jwtRequestInterceptor(GeneratorTokenBiFunction generatorTokenBiFunction) {
        return template -> template.header(HttpHeaders.AUTHORIZATION, generatorTokenBiFunction.apply(null, SERVICE_TOKEN_ISSUER));
    }


}
