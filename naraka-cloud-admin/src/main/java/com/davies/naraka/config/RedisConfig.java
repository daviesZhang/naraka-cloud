package com.davies.naraka.config;

import com.google.common.base.Strings;
import com.google.common.base.Verify;
import com.davies.naraka.config.properties.RedissonProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author davies
 * @date 2022/2/21 10:18 AM
 */
@EnableCaching
@Configuration
public class RedisConfig {

    private final RedissonProperties redissonProperties;

    public RedisConfig(RedissonProperties redissonProperties) {
        this.redissonProperties = redissonProperties;
    }

    /**
     * 根据getAddress数量支持两种模式
     * Single
     * Cluster
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        Verify.verify(!redissonProperties.getAddress().isEmpty(), "app.redisson.address必须配置");
        if (redissonProperties.getAddress().size()==1){
            SingleServerConfig singleServerConfig= config.useSingleServer()
                    .setAddress(redissonProperties.getAddress().get(0));
            if (!Strings.isNullOrEmpty(redissonProperties.getPassword())){
                singleServerConfig.setPassword(redissonProperties.getPassword());
            }
        }else{
            ClusterServersConfig clusterServersConfig=   config.useClusterServers()
                    .addNodeAddress(redissonProperties.getAddress().toArray(new String[]{}));
            if (!Strings.isNullOrEmpty(redissonProperties.getPassword())){
                clusterServersConfig.setPassword(redissonProperties.getPassword());
            }
        }
        return Redisson.create(config);
    }

    @Bean
    CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = new HashMap<String, CacheConfig>();
        // 创建一个名称为"testMap"的缓存，过期时间ttl为24分钟，同时最长空闲时maxIdleTime为12分钟。
        //config.put("testMap", new CacheConfig(24*60*1000, 12*60*1000));
        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
