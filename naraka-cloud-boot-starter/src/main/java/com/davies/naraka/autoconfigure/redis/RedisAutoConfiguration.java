package com.davies.naraka.autoconfigure.redis;

import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import com.davies.naraka.autoconfigure.HasResources;
import com.davies.naraka.autoconfigure.ProcessorFunction;
import com.davies.naraka.autoconfigure.properties.RedisProperties;
import com.google.common.base.Strings;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author davies
 * @date 2022/2/28 3:00 PM
 */
@Configuration
@ConditionalOnProperty(value = "naraka.redis.address")
@ConditionalOnClass(name = {"org.redisson.api.RedissonClient"})
@EnableConfigurationProperties(RedisProperties.class)
public class RedisAutoConfiguration {

    
    private final RedisProperties redisProperties;


    public RedisAutoConfiguration(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }


    @Bean
    @ConditionalOnMissingBean(ProcessorFunction.class)
    public ProcessorFunction processorFunction(CurrentUserNameSupplier currentUserNameSupplier, RedissonClient redissonClient) {
        return new RedisProcessorFunction(currentUserNameSupplier, redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean(HasResources.class)
    public HasResources hasResources(RedissonClient redissonClient) {
        return new RedisHasResources(redissonClient);
    }

    /**
     * 根据getAddress数量支持两种模式
     * Single
     * Cluster
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
   public RedissonClient redisson() throws IOException {
        Config config = new Config();
        if (redisProperties.getAddress().size()==1){
            SingleServerConfig singleServerConfig= config.useSingleServer()
                    .setAddress(redisProperties.getAddress().get(0));
            if (!Strings.isNullOrEmpty(redisProperties.getPassword())){
                singleServerConfig.setPassword(redisProperties.getPassword());
            }
        }else{
            ClusterServersConfig clusterServersConfig= config.useClusterServers()
                    .addNodeAddress(redisProperties.getAddress().toArray(new String[]{}));
            if (!Strings.isNullOrEmpty(redisProperties.getPassword())){
                clusterServersConfig.setPassword(redisProperties.getPassword());
            }
        }
        return Redisson.create(config);
    }




}
