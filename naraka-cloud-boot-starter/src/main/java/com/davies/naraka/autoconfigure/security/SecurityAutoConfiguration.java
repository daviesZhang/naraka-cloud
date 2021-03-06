package com.davies.naraka.autoconfigure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Verification;
import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import com.davies.naraka.autoconfigure.GeneratorTokenBiFunction;
import com.davies.naraka.autoconfigure.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    @ConditionalOnClass(name = "org.aspectj.lang.JoinPoint")
    public CheckHasUserAspect checkHasUserAspect(@Autowired(required = false) CurrentUserNameSupplier currentUserNameSupplier) {
        return new CheckHasUserAspect(currentUserNameSupplier);
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ???????????????????????????jwt token
     *
     * @return BiFunction<?????????, ?????????, token>
     */
    @Bean
    @ConditionalOnMissingBean(GeneratorTokenBiFunction.class)
    @ConditionalOnClass(name = "com.auth0.jwt.JWT")
    public GeneratorTokenBiFunction generatorToken(Algorithm algorithm) {
        return (String username, String issuedUser) -> JWT.create().withSubject(username)
                .withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(this.securityProperties.getExpiresAt())
                        .atZone(ZoneOffset.systemDefault()).toInstant()))
                .withIssuedAt(new Date())
                .withIssuer(issuedUser)
                .sign(algorithm);
    }
}
