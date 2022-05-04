package com.davies.naraka.admin.config;

import com.davies.naraka.autoconfigure.redis.RedisGenerateId;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author davies
 * @date 2022/2/21 10:18 AM
 */
@EnableCaching
@Configuration
public class RedisConfig {


    /*@Bean
    CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = new HashMap<String, CacheConfig>();
        // 创建一个名称为"testMap"的缓存，过期时间ttl为24分钟，同时最长空闲时maxIdleTime为12分钟。
        //config.put("testMap", new CacheConfig(24*60*1000, 12*60*1000));
        return new RedissonSpringCacheManager(redissonClient, config);
    }*/

    @Autowired
    private RedissonClient redissonClient;

    @Bean
    public RIdGenerator generateId() {
        RIdGenerator rIdGenerator = new RedisGenerateId(redissonClient).generateId("admin");

        return rIdGenerator;

    }
}
