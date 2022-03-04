package com.davies.naraka.autoconfigure.servlet;

import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import com.davies.naraka.autoconfigure.RemoteUserFilter;
import com.davies.naraka.autoconfigure.RequestRemoteUserSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author davies
 * @date 2022/2/28 3:19 PM
 */


@Configuration
@ConditionalOnClass(name = "javax.servlet.http.HttpServletRequest")
public class ServletAutoConfiguration {


    @Bean
    @ConditionalOnProperty(name = "naraka.username.header")
    @ConditionalOnMissingBean(RemoteUserFilter.class)
    public RemoteUserFilter remoteUserFilter() {
        return new RemoteUserFilter();
    }


    @Bean
    @ConditionalOnMissingBean(CurrentUserNameSupplier.class)
    public CurrentUserNameSupplier currentUserNameSupplier() {
        return new RequestRemoteUserSupplier();
    }


}
