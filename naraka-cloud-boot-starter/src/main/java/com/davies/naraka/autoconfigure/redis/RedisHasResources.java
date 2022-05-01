package com.davies.naraka.autoconfigure.redis;

import com.davies.naraka.autoconfigure.HasResources;
import com.davies.naraka.autoconfigure.security.SecurityHelper;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CompletableFuture;

/**
 * 从redis里面验证用户是否包含资源的方法
 * @author davies
 * @date 2022/2/28 4:14 PM
 */
public class RedisHasResources implements HasResources {


    private final RedissonClient redissonClient;

    public RedisHasResources(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public CompletableFuture<Boolean> apply(String resource, String principal) {
        String key = SecurityHelper.userAuthorityApiCacheKey(principal);
        RMap<String, Object> map = redissonClient.getMap(key);
        return map.containsKeyAsync(resource).toCompletableFuture();
    }
}
