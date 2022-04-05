package com.davies.naraka.puppeteer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author sycho
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.davies.naraka.puppeteer.repository")
public class NarakaCloudPuppeteerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NarakaCloudPuppeteerApplication.class, args);
    }


}
