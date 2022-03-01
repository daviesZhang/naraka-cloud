package com.davies.naraka.autoconfigure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Verification;
import com.davies.naraka.autoconfigure.GeneratorTokenBiFunction;
import com.davies.naraka.autoconfigure.HasResources;
import com.davies.naraka.autoconfigure.RedisHasResources;
import com.davies.naraka.autoconfigure.properties.SecurityProperties;
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
    @ConditionalOnMissingBean(HasResources.class)
    @ConditionalOnClass(name = {"org.redisson.api.RedissonClient"})
    public HasResources hasResources(){
        return new RedisHasResources();
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
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 根据用户名构造一个jwt token
     *
     * @return BiFunction<用户名, 签发人, token>
     */
    @Bean
    @ConditionalOnMissingBean(GeneratorTokenBiFunction.class)
    public GeneratorTokenBiFunction generatorToken(Algorithm algorithm) {
        return (String username, String issuedUser) -> JWT.create().withSubject(username)
                .withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(this.securityProperties.getExpiresAt())
                        .atZone(ZoneOffset.systemDefault()).toInstant()))
                .withIssuedAt(new Date())
                .withIssuer(issuedUser)
                .sign(algorithm);
    }
}
