package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.cloud.common.StringConstants;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.CustomType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

/**
 * @author davies
 * @date 2022/3/9 10:21 PM
 */
@Slf4j
public class EnumCodeTypeContributor implements TypeContributor {

    private final String typeEnumsPackage;

    public EnumCodeTypeContributor(String typeEnumsPackage) {
        this.typeEnumsPackage = typeEnumsPackage;
    }

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        Set<Class<?>> classSet = getEnumCodeClass();
        for (Class<?> aClass : classSet) {
            typeContributions.contributeType(new CustomType(new EnumCodeUserType(aClass), new String[]{aClass.getSimpleName()}));
        }
    }

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();


    private Set<Class<?>> getEnumCodeClass() {
        if (!Strings.isNullOrEmpty(typeEnumsPackage)) {
            Set<Class<?>> classes;
            if (typeEnumsPackage.contains(StringConstants.STAR) && !typeEnumsPackage.contains(StringConstants.COMMA)
                    && !typeEnumsPackage.contains(StringConstants.SEMICOLON)) {
                try {
                    classes = this.scanClasses(typeEnumsPackage, EnumCodePersistence.class);
                } catch (IOException e) {
                    throw new RuntimeException("Cannot scan class in '[" + typeEnumsPackage + "]' package", e);
                }
                if (classes.isEmpty()) {
                    log.warn("Can't find class in '[" + typeEnumsPackage + "]' package. Please check your configuration.");
                }
            } else {
                classes = new HashSet<>();
                String[] typeEnumsPackageArray = tokenizeToStringArray(typeEnumsPackage,
                        ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
                Assert.notNull(typeEnumsPackageArray, "not find typeEnumsPackage:" + typeEnumsPackage);
                Stream.of(typeEnumsPackageArray).forEach(typePackage -> {
                    try {
                        Set<Class<?>> scanTypePackage = scanClasses(typePackage, EnumCodePersistence.class);
                        if (scanTypePackage.isEmpty()) {
                            log.warn("Can't find class in '[" + typePackage + "]' package. Please check your configuration.");
                        } else {
                            classes.addAll(scanTypePackage);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Cannot scan class in '[" + typePackage + "]' package", e);
                    }
                });
            }
            return classes.stream().filter(Class::isEnum)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }


    private Set<Class<?>> scanClasses(String packagePatterns, Class<?> assignableType) throws IOException {
        Set<Class<?>> classes = new HashSet<>();
        String[] packagePatternArray = tokenizeToStringArray(packagePatterns,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        for (String packagePattern : packagePatternArray) {
            Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + ClassUtils.convertClassNameToResourcePath(packagePattern) + "/**/*.class");
            for (Resource resource : resources) {
                try {
                    ClassMetadata classMetadata = METADATA_READER_FACTORY.getMetadataReader(resource).getClassMetadata();
                    Class<?> clazz = Class.forName(classMetadata.getClassName());
                    if (assignableType == null || assignableType.isAssignableFrom(clazz)) {
                        classes.add(clazz);
                    }
                } catch (Throwable e) {
                    log.warn("Cannot load the '" + resource + "'. Cause by " + e.toString());
                }
            }
        }
        return classes;
    }
}
