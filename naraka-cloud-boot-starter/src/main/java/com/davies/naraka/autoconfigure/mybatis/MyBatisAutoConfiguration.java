package com.davies.naraka.autoconfigure.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import com.davies.naraka.autoconfigure.properties.EncryptProperties;
import com.davies.naraka.autoconfigure.properties.MyBatisProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author davies
 * @date 2022/2/28 8:10 PM
 */
@Configuration
@ConditionalOnClass(name = "com.baomidou.mybatisplus.core.conditions.query.QueryWrapper")
@EnableConfigurationProperties({EncryptProperties.class, MyBatisProperties.class})
public class MyBatisAutoConfiguration {

    final EncryptProperties encryptProperties;
    final MyBatisProperties myBatisProperties;

    public MyBatisAutoConfiguration(EncryptProperties encryptProperties, MyBatisProperties myBatisProperties) {
        this.encryptProperties = encryptProperties;
        this.myBatisProperties = myBatisProperties;
    }

    @ConditionalOnProperty(value = "naraka.encrypt.enable", havingValue = "true")
    @ConditionalOnMissingBean(name = "resultCryptoInterceptor")
    @Bean
    public ResultCryptoInterceptor resultCryptoInterceptor() {
        return new ResultCryptoInterceptor(encryptProperties);
    }

    @ConditionalOnProperty(value = "naraka.encrypt.enable", havingValue = "true")
    @ConditionalOnMissingBean(name = "paramsCryptoInterceptor")
    @Bean
    public ParamsCryptoInterceptor paramsCryptoInterceptor() {
        return new ParamsCryptoInterceptor(encryptProperties);
    }

    @ConditionalOnProperty(value = "naraka.mybatis.metaObjectFill", havingValue = "true",matchIfMissing = true)
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    @ConditionalOnBean(CurrentUserNameSupplier.class)
    @Bean
    public MyBatisMetaObjectHandler myBatisMetaObjectHandler(CurrentUserNameSupplier currentUserNameSupplier) {
        return new MyBatisMetaObjectHandler(currentUserNameSupplier, myBatisProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "mybatisPlusInterceptor")
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @ConditionalOnMissingBean(name = "myBatisQueryUtils")
    @Bean
    public MyBatisQueryUtils myBatisQueryUtils() {
        return new MyBatisQueryUtils(encryptProperties);
    }
}
