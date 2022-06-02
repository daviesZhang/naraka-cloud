package com.davies.naraka.system.config;

import com.davies.naraka.autoconfigure.redis.RedisIdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author davies
 * @date 2022/5/30 01:56
 */
@Slf4j
@Service
public class IdGenerator implements IdentifierGenerator {


    private final RedisIdGenerate redisIdGenerate;


    public IdGenerator(RedisIdGenerate redisIdGenerate) {
        this.redisIdGenerate = redisIdGenerate;
    }


    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return this.redisIdGenerate.nextUUID(object);
    }


}
