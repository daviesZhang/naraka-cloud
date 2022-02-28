package com.davies.naraka.gateway;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.davies.naraka.autoconfigure.HasResources;
import com.davies.naraka.cloud.common.StringConstants;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;
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

    private final HasResources hasResources;


    public TokenRoutePredicateFactory(Algorithm algorithm, HasResources hasResources) {
        super(TokenRoutePredicateFactory.Config.class);
        this.jwtVerifier = JWT.require(algorithm).build();
        this.hasResources = hasResources;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("name");
    }

    /**
     * 对token进行验证,并把获取到的用户名放入exchange变量,供后面过滤器使用
     *
     * @param config
     * @return
     */
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
                    Map<String, String> variables = new HashMap<>(1);
                    //当Subject不为空时意味着用户调用,需要鉴权.当签发者属于serviceIssuer,意味着是服务间互相调用,直接允许通过
                    if (!Strings.isNullOrEmpty(decoded.getSubject())) {
                        String resource = exchange.getRequest().getMethodValue().toLowerCase() + StringConstants.SPACE + exchange.getRequest().getURI().getPath();

                        variables.put(config.getName(), decoded.getSubject());
                        ServerWebExchangeUtils.putUriTemplateVariables(exchange, variables);
                        return Mono.fromCompletionStage(hasResources.test(resource, decoded.getSubject()));
                    } else if (Objects.equals(decoded.getIssuer(), serviceIssuer)) {
                        variables.put(config.getName(), StringConstants.EMPTY);
                    } else {
                        return Mono.just(false);
                    }
                    ServerWebExchangeUtils.putUriTemplateVariables(exchange, variables);
                    return Mono.just(true);
                } catch (Exception e) {
                    //todo 鉴权错误
                    return Mono.just(false);
                }
            }


            @Override
            public String toString() {
                return String.format("name: %s", config.getName());
            }
        };
    }


    /**
     * 考虑需要从redis中获取数据鉴权,只允许异步方法
     *
     * @param config
     * @return
     */
    @Override
    public Predicate<ServerWebExchange> apply(TokenRoutePredicateFactory.Config config) {
        throw new UnsupportedOperationException(
                "TokenRoutePredicateFactory is only async.");
    }

    public static class Config {
        private String name;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Config{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
