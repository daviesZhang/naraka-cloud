package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.autoconfigure.properties.EncryptProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "org.springframework.data.jpa.domain.Specification")
@EnableConfigurationProperties({EncryptProperties.class})
public class JpaAutoConfiguration {

    private final EncryptProperties encryptProperties;

    public JpaAutoConfiguration(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }

    @Bean
    @ConditionalOnMissingBean(name = "specificationUtils")
    public JpaSpecificationUtils specificationUtils() {
        return new JpaSpecificationUtils(this.encryptProperties);
    }
}
