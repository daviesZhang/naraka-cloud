package com.davies.naraka.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author davies
 * @date 2022/3/2 1:21 PM
 */
@Data
@ConfigurationProperties("naraka.gateway")
@Component
public class NarakaGatewayProperties {


    private List<String> ignoreAuthorization = Collections.emptyList();
}
