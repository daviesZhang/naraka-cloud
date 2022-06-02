package com.davies.naraka.admin.config;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.davies.naraka.autoconfigure.redis.RedisIdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author davies
 * @date 2022/5/1 16:36
 */
@Component
@Slf4j
public class RedisIdentifierGenerator implements IdentifierGenerator {


    private final RedisIdGenerate idGenerate;


    @Value("${worker.id:}")
    private Integer workerId;

    @Value("${dataCenter.id:}")
    private Integer dataCenterId;
    DefaultIdentifierGenerator defaultIdentifierGenerator;

    public RedisIdentifierGenerator(RedisIdGenerate redisIdGenerate) {
        this.idGenerate = redisIdGenerate;
        if (workerId == null || dataCenterId == null) {
            this.defaultIdentifierGenerator = new DefaultIdentifierGenerator();
        } else {
            this.defaultIdentifierGenerator = new DefaultIdentifierGenerator(workerId, dataCenterId);
        }
    }


    @Override
    public Number nextId(Object entity) {
        return this.defaultIdentifierGenerator.nextId(entity);
    }

    @Override
    public String nextUUID(Object entity) {
        return this.idGenerate.nextUUID(entity);
    }
}
