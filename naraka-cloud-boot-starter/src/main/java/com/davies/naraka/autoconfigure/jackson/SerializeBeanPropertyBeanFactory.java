package com.davies.naraka.autoconfigure.jackson;

import org.springframework.context.ApplicationContext;

/**
 * 根据bean name返回SerializeBeanPropertyFunction的bean
 *
 * @author davies
 * @date 2022/3/4 1:51 PM
 */
public class SerializeBeanPropertyBeanFactory implements SerializeBeanPropertyFactory {


    private final ApplicationContext applicationContext;

    public SerializeBeanPropertyBeanFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public SerializeBeanPropertyFunction apply(String s) {
        return applicationContext.getBean(s, SerializeBeanPropertyFunction.class);

    }
}
