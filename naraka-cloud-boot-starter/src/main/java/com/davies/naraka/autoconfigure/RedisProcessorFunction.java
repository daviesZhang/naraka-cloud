package com.davies.naraka.autoconfigure;

import com.davies.naraka.cloud.common.enums.AuthorityProcessorType;
import com.google.common.base.Strings;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 从redis里面获取用户拥有的资源细节
 * @author davies
 * @date 2022/2/27 8:55 PM
 */

public class RedisProcessorFunction implements ProcessorFunction {

    @Autowired
    private CurrentUserNameSupplier currentUserNameSupplier;
    @Autowired
    private  RedissonClient redissonClient;




    @Override
    public Map<String, Set<AuthorityProcessorType>> apply(String key) {
        String username = currentUserNameSupplier.get();
        if (Strings.isNullOrEmpty(username)) {
            return Collections.emptyMap();
        }
        RMap<String, Map<String, Set<AuthorityProcessorType>>>
                rMap = this.redissonClient.getMap(SecurityHelper.userAuthorityCacheKey(username));
        return rMap.getOrDefault(key, Collections.emptyMap());

    }
}
