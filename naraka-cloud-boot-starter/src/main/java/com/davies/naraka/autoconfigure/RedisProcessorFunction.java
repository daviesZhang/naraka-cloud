package com.davies.naraka.autoconfigure;

import com.google.common.base.Strings;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 从redis里面获取用户拥有的资源细节
 * @author davies
 * @date 2022/2/27 8:55 PM
 */

public class RedisProcessorFunction implements ProcessorFunction {


    private final CurrentUserNameSupplier currentUserNameSupplier;

    private final RedissonClient redissonClient;

    public RedisProcessorFunction(CurrentUserNameSupplier currentUserNameSupplier, RedissonClient redissonClient) {
        this.currentUserNameSupplier = currentUserNameSupplier;
        this.redissonClient = redissonClient;
    }


    @Override
    public Map<String, Set<String>> apply(String key) {
        String username = currentUserNameSupplier.get();
        if (Strings.isNullOrEmpty(username)) {
            return Collections.emptyMap();
        }
        RMap<String, Map<String, Set<String>>>
                rMap = this.redissonClient.getMap(SecurityHelper.userAuthorityCacheKey(username));

        return rMap.getOrDefault(key, Collections.emptyMap());

    }
}
