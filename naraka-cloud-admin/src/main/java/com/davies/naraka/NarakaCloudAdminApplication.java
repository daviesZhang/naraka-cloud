package com.davies.naraka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NarakaCloudAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(NarakaCloudAdminApplication.class, args);
    }

}
