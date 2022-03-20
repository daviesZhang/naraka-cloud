package com.davies.naraka.rules.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

/**
 * @author davies
 * @date 2022/3/17 19:40
 */
@Configuration
public class DroolsConfig {
    private static final String RULES_PATH = "rules/";

    @Bean
    public KieFileSystem kieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = kieServices().newKieFileSystem();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourcePatternResolver.getResources("classpath*:" + RULES_PATH + "**/*.*");
        for (Resource file : resources) {
            kieFileSystem.write(ResourceFactory.newUrlResource(file.getURL()));
        }
        return kieFileSystem;

    }

    @Bean
    public KieContainer kieContainer(KieFileSystem kieFileSystem) throws IOException {
        final KieRepository kieRepository = kieRepository();
        KieBuilder kieBuilder = kieServices().newKieBuilder(kieFileSystem);

        kieBuilder.buildAll();
        return kieServices().newKieContainer(kieRepository.getDefaultReleaseId());
    }

    private KieRepository kieRepository() {
        final KieRepository kieRepository = kieServices().getRepository();
        kieRepository.addKieModule(kieRepository::getDefaultReleaseId);
        return kieRepository;
    }

    private KieServices kieServices() {
        return KieServices.Factory.get();
    }
}
