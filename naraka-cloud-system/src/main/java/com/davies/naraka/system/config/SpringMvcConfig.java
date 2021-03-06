package com.davies.naraka.system.config;

import com.davies.naraka.autoconfigure.jackson.MappingJacksonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author davies
 * @date 2022/1/22 10:34 AM
 */
@Configuration
@EnableWebMvc
public class SpringMvcConfig implements WebMvcConfigurer {


    @Autowired
    private MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
//        configurer.addPathPrefix("/admin/system", HandlerTypePredicate.forBasePackage("com.davies.naraka.admin.controller.system"));
        configurer.addPathPrefix("/system", HandlerTypePredicate.forAnnotation(RestController.class));
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        converters.add(mappingJacksonHttpMessageConverter);
    }


}
