package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import com.davies.naraka.autoconfigure.properties.EncryptProperties;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.spi.TypeContributorList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 使用Hibernate 实现JPA的时候,提供一些列使用自动装配
 *
 * @author davies
 * @see EnumCodeTypeContributor 自动枚举-code映射转换,需配置枚举所在包,多个用;隔开 naraka.jpa.typeEnumsPackage
 * @see EnumCodePersistence
 * 自定义枚举需实现EnumCodePersistence接口后
 * //@Type(type = "Status") //type为枚举类的simpleName
 * // private Status scriptStatus;
 * @see CryptoStringTypeContributor 字符串出库入库加解密
 * //@Type(type = "crypto_phone")
 * //private String phone; //将使用phone的密钥对该字段进行加解密,请配置 naraka.encrypt.keyMap.phone
 * //@Type(type = "crypto")
 * //private String email; //将使用默认的密钥对该字段进行加解密,请配置 naraka.encrypt.defaultKey
 * @see JpaSpecificationUtils Specification简单对象查询方法
 * @see com.davies.naraka.autoconfigure.domain.QueryField
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
    @ConditionalOnProperty(name = "naraka.encrypt.enable", havingValue = "true")
    public CryptoStringTypeContributor cryptoStringTypeContributor() {
        return new CryptoStringTypeContributor(this.encryptProperties.getKeyMap(), this.encryptProperties.getDefaultKey());
    }

    @Bean
    @ConditionalOnBean(CurrentUserNameSupplier.class)
    @ConditionalOnProperty(name = "naraka.jpa.usernameAuditor", havingValue = "true", matchIfMissing = true)
    public UserNameAuditorAware auditorProvider(CurrentUserNameSupplier supplier) {
        return new UserNameAuditorAware(supplier);
    }

    @Bean
    @ConditionalOnProperty(name = "naraka.jpa.typeEnumsPackage")
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(List<TypeContributor> typeContributorList) {
        return hibernateProperties -> hibernateProperties.put(EntityManagerFactoryBuilderImpl.TYPE_CONTRIBUTORS, (TypeContributorList) () -> typeContributorList);
    }
}
