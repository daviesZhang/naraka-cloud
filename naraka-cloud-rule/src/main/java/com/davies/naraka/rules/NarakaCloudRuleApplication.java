package com.davies.naraka.rules;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author davies
 * @date 2022/3/17 19:22
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableDiscoveryClient
public class NarakaCloudRuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(NarakaCloudRuleApplication.class, args);
    }
}
