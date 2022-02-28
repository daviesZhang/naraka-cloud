package com.davies.naraka.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author davies
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NarakaCloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(NarakaCloudGatewayApplication.class, args);
    }



}
