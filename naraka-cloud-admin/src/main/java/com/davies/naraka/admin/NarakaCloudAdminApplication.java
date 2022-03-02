package com.davies.naraka.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@MapperScan("com.davies.naraka.admin.mapper")
public class NarakaCloudAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(NarakaCloudAdminApplication.class, args);
    }

}
