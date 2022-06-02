package com.davies.naraka.autoconfigure.redis;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author davies
 * @date 2022/5/1 16:10
 */
public class RedisIdGenerate {

    private final LoadingCache<String, RIdGenerator> cache;

    public RedisIdGenerate(RedissonClient redissonClient) {
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS)
                .build(new CacheLoader<String, RIdGenerator>() {
                    @Override
                    public @NotNull RIdGenerator load(@NotNull String key) {
                        RIdGenerator idGenerator = redissonClient.getIdGenerator(key);
                        idGenerator.tryInit(100000, 1000);
                        idGenerator.expire(8, TimeUnit.SECONDS);
                        return idGenerator;
                    }
                });
    }

    public String nextUUID(@Nullable Object entity) {
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        RIdGenerator idGenerator;
        if (entity != null) {
            idGenerator = cache.getUnchecked(time + entity.getClass().getName());
        } else {
            idGenerator = cache.getUnchecked(time);
        }
        return time + idGenerator.nextId();
    }


}
