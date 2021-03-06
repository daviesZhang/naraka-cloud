package com.davies.naraka.gateway;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.davies.naraka.autoconfigure.HasResources;
import com.davies.naraka.cloud.common.StringConstants;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author davies
 * @date 2022/3/2 12:05 PM
 */
@Slf4j
public class TokenFilter implements GlobalFilter, Ordered {

    private final JWTVerifier jwtVerifier;


    private final HasResources hasResources;

    private static final String USERNAME_HEADER_NAME = "username";

    private final NarakaGatewayProperties narakaGatewayProperties;


    public TokenFilter(Algorithm algorithm, HasResources hasResources, NarakaGatewayProperties narakaGatewayProperties) {
        this.jwtVerifier = JWT.require(algorithm).build();
        this.hasResources = hasResources;
        this.narakaGatewayProperties = narakaGatewayProperties;
    }

    /**
     * 跟服务约定 当Issuer等于serviceIssuer,意味着是服务间互相调用,直接允许通过
     * 当Issuer不等于serviceIssuer时意味着用户调用,需要对Subject鉴权.
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String resource = exchange.getRequest().getMethodValue().toLowerCase() + StringConstants.SPACE + exchange.getRequest().getURI().getPath();
        if (this.narakaGatewayProperties.getIgnoreAuthorization().contains(resource)) {
            return chain.filter(exchange);
        }
        if (Strings.isNullOrEmpty(token)) {
            return unauthorized(exchange);
        }
        try {
            DecodedJWT decoded = jwtVerifier.verify(token);
            if (Objects.equals(decoded.getIssuer(), StringConstants.SERVICE_TOKEN_ISSUER)) {
                if (!Strings.isNullOrEmpty(decoded.getSubject())) {
                    return setUsernameAndNext(exchange, chain, decoded.getSubject());
                } else {
                    return chain.filter(exchange);
                }
            } else if (!Strings.isNullOrEmpty(decoded.getSubject())) {
                return Mono.fromCompletionStage(hasResources.apply(resource, decoded.getSubject()))
                        .flatMap(aBoolean -> {
                            if (aBoolean) {
                                return setUsernameAndNext(exchange, chain, decoded.getSubject());
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug("[{}]-[{}]鉴权失败~", decoded.getSubject(), resource);
                                }
                                return forbidden(exchange);
                            }
                        });
            }
            return unauthorized(exchange);
        } catch (JWTVerificationException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
            return unauthorized(exchange);
        }
    }

    private Mono<Void> setUsernameAndNext(ServerWebExchange exchange, GatewayFilterChain chain, String username) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(httpHeaders -> httpHeaders.set(USERNAME_HEADER_NAME, username)).build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
