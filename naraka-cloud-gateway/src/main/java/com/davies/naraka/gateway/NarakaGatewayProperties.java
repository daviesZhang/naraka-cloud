package com.davies.naraka.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * @author davies
 * @date 2022/3/2 1:21 PM
 */
@Data
@ConfigurationProperties("naraka.gateway")
public class NarakaGatewayProperties {


    private List<String> ignoreAuthorization = Collections.emptyList();
}
