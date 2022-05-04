package com.davies.naraka.admin.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author davies
 * @date 2022/5/1 16:36
 */
@Component
@Slf4j
public class RedisIdentifierGenerator implements IdentifierGenerator {

    private final RedissonClient redissonClient;


    private static final long init = 1651405407;

    private String machineCode;
    private final LoadingCache<String, RIdGenerator> cache;

    public RedisIdentifierGenerator(RedissonClient redissonClient,
                                    @Value("${app.machine.code:100}") String machineCode) {
        this.redissonClient = redissonClient;

        this.machineCode = machineCode;
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS)
                .build(new CacheLoader<String, RIdGenerator>() {
                    @Override
                    public @NotNull RIdGenerator load(@NotNull String key) {
                        RIdGenerator idGenerator = redissonClient.getIdGenerator(key);
                        idGenerator.tryInit(100000, 1000);
                        idGenerator.expire(10, TimeUnit.SECONDS);
                        return idGenerator;
                    }
                });
    }


    @Override
    public Number nextId(Object entity) {
        RIdGenerator idGenerator = cache.getUnchecked(entity.getClass().getName());
        return idGenerator.nextId();
    }


    @Override
    public String nextUUID(Object entity) {
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        RIdGenerator idGenerator = cache.getUnchecked(time + entity.getClass().getName());
        return time + idGenerator.nextId();
    }
}
