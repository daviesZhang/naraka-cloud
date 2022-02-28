package com.davies.naraka.autoconfigure;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

/**
 * @author davies
 * @date 2022/2/28 4:14 PM
 */
public class RedisHasResources implements HasResources {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public CompletableFuture<Boolean> test(String resource, String principal) {
        String key = SecurityHelper.userAuthorityCacheKey(principal);
        RMap<String, Object> map = redissonClient.getMap(key);
        return map.containsKeyAsync(resource).toCompletableFuture();
    }
}
