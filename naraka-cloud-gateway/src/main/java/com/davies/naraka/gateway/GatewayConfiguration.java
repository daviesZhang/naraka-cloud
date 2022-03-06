package com.davies.naraka.gateway;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.auth0.jwt.algorithms.Algorithm;
import com.davies.naraka.autoconfigure.HasResources;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * @author davies
 * @date 2022/3/2 2:29 PM
 */
@Configuration
@EnableConfigurationProperties(NarakaGatewayProperties.class)
public class GatewayConfiguration {


    private final NarakaGatewayProperties gatewayProperties;

    public GatewayConfiguration(NarakaGatewayProperties gatewayProperties) {

        this.gatewayProperties = gatewayProperties;
    }

    @Bean
    public GlobalFilter tokenFilter(Algorithm algorithm, HasResources hasResource) {
        return new TokenFilter(algorithm, hasResource, this.gatewayProperties);
    }

    @PostConstruct
    public void doInit() {
        initCustomizedApis();
        initGatewayRules();
    }

    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();
       /* ApiDefinition api1 = new ApiDefinition("refreshToken_api")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/admin/refreshToken"));
                }});
        definitions.add(api1);*/
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }

    /**
     *
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
//        测试 加一个限流规则,不允许频繁刷新token
//        rules.add(new GatewayFlowRule("refreshToken_api")
//                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID)
//                .setCount(2)
//                .setIntervalSec(60 * 10)
//                .setParamItem(new GatewayParamFlowItem()
//                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HEADER)
//                        .setFieldName(HttpHeaders.AUTHORIZATION)
//
//                )
//        );
        GatewayRuleManager.loadRules(rules);
    }
}
