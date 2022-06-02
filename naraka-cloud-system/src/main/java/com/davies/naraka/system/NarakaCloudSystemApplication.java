package com.davies.naraka.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author davies
 * @date 2022/5/17 14:15
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAspectJAutoProxy
@EnableJpaAuditing
public class NarakaCloudSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(NarakaCloudSystemApplication.class, args);
    }

}
