package com.davies.narakacloudgateway;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.cloud.gateway.handler.predicate.ReadBodyRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author davies
 * @date 2022/2/26 9:12 PM
 */
@Component
public class TokenRoutePredicateFactory extends AbstractRoutePredicateFactory<TokenRoutePredicateFactory.Config> {


    private JWTVerifier jwtVerifier;

    @Value("${jwt.token.serviceIssuer:}")
    private String serviceIssuer;


    public TokenRoutePredicateFactory(@Value("${jwt.token.secret}") String secret) {
        super(TokenRoutePredicateFactory.Config.class);
        this.jwtVerifier = JWT.require(Algorithm.HMAC512(secret)).build();
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("token");
    }

    @Override
    public AsyncPredicate<ServerWebExchange> applyAsync(TokenRoutePredicateFactory.Config config) {
        return new AsyncPredicate<ServerWebExchange>() {
            @Override
            public Publisher<Boolean> apply(ServerWebExchange exchange) {
                String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (Strings.isNullOrEmpty(token)) {
                    return Mono.just(false);
                }
                try {
                    DecodedJWT decoded = jwtVerifier.verify(token);
                    if(Objects.equals(serviceIssuer, decoded.getIssuer())){
                        return Mono.just(true);
                    }else{
                        //todo 根据url和 decoded.getSubject() 通过redis查询鉴权数据,判断是否允许
                        return Mono.just(false);
                    }
                } catch (Exception e) {
                    //todo 鉴权错误
                    return Mono.just(false);
                }
            }


            @Override
            public String toString() {
                return String.format("Tokens: %s", config.getToken());
            }
        };
    }


    /**
     * 考虑需要从redis中获取数据鉴权,只允许异步方法
     * @param config
     * @return
     */
    @Override
    public Predicate<ServerWebExchange> apply(TokenRoutePredicateFactory.Config config) {
        throw new UnsupportedOperationException(
                "TokenRoutePredicateFactory is only async.");
    }

    public static class Config {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "Config{" +
                    "token='" + token + '\'' +
                    '}';
        }
    }
}
