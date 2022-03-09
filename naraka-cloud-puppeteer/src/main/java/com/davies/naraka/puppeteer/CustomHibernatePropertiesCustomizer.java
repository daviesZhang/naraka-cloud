package com.davies.naraka.puppeteer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author davies
 */
@Component
public class CustomHibernatePropertiesCustomizer implements HibernatePropertiesCustomizer {
    @Autowired
    private UserTypeIntegrator userTypeIntegrator;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
//        hibernateProperties.put("hibernate.session_factory.interceptor", "com.davies.naraka.puppeteer.UserTypeIntegrator");
//        hibernateProperties.put("hibernate.session_factory.interceptor",userTypeIntegrator);
    }
}
