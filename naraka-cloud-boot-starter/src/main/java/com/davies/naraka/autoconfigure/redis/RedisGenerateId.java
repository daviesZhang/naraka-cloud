package com.davies.naraka.autoconfigure.redis;

import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;

/**
 * @author davies
 * @date 2022/5/1 16:10
 */
public class RedisGenerateId {


    private final RedissonClient redissonClient;

    public RedisGenerateId(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public RIdGenerator generateId(String name) {
        return this.redissonClient.getIdGenerator(name);

    }
}
