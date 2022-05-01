package com.davies.naraka.autoconfigure.redis;

import com.davies.naraka.autoconfigure.AuthorityRowFunction;
import com.davies.naraka.autoconfigure.security.SecurityHelper;
import com.davies.naraka.cloud.common.domain.AuthorityRow;
import com.google.common.collect.Lists;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.List;

/**
 * @author davies
 * @date 2022/4/8 22:05
 */
public class RedisAuthorityRowFunction implements AuthorityRowFunction {

    private final RedissonClient redissonClient;

    public RedisAuthorityRowFunction(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public List<AuthorityRow> apply(String resource, String principal) {
        String key = SecurityHelper.userAuthorityRowCacheKey(principal);
        RMap<String, List<AuthorityRow>> map = redissonClient.getMap(key);
        return map.getOrDefault(resource, Lists.newArrayList());
    }

}
