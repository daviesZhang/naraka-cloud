package com.davies.naraka.puppeteer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author sycho
 */
@SpringBootApplication
@EnableJpaAuditing
public class NarakaCloudPuppeteerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NarakaCloudPuppeteerApplication.class, args);
    }



}
