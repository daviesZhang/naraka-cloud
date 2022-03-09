package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.autoconfigure.properties.EncryptProperties;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.jpa.boot.spi.TypeContributorList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author davies
 */
@Configuration
@ConditionalOnClass(name = "org.springframework.data.jpa.domain.Specification")
@EnableConfigurationProperties({EncryptProperties.class})
public class JpaAutoConfiguration {

    private final EncryptProperties encryptProperties;

    @Value("${naraka.jpa.typeEnumsPackage:}")
    public String typeEnumsPackage;

    public JpaAutoConfiguration(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }

    @Bean
    @ConditionalOnMissingBean(name = "specificationUtils")
    public JpaSpecificationUtils specificationUtils() {
        return new JpaSpecificationUtils(this.encryptProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "naraka.jpa.typeEnumsPackage")
    public EnumCodeTypeContributor enumCodeTypeContributor() {
        return new EnumCodeTypeContributor(this.typeEnumsPackage);
    }

    @Bean
    @ConditionalOnMissingBean(HibernatePropertiesCustomizer.class)
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(List<TypeContributor> typeContributorList) {
        return hibernateProperties -> hibernateProperties.put("hibernate.type_contributors", (TypeContributorList) () -> typeContributorList);
    }
}
