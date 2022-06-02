package com.davies.naraka.gateway;

import com.davies.naraka.autoconfigure.HasResources;
import com.davies.naraka.autoconfigure.security.SecurityHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 从redis里面验证用户是否包含资源的方法
 *
 * @author davies
 * @date 2022/2/28 4:14 PM
 */
@Slf4j
@Component
public class AuthHasResources implements HasResources {


    private final RedissonClient redissonClient;

    private final ObjectMapper objectMapper;

    private static final String ROOT = "root";

    public AuthHasResources(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        objectMapper = new ObjectMapper();
    }

    @Override
    public CompletableFuture<Boolean> apply(String resource, String principal) {

        if (ROOT.equals(principal)) {
            return CompletableFuture.completedFuture(true);
        }

        String key = SecurityHelper.userAuthorityApiCacheKey(principal);
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.getAsync()
                .toCompletableFuture().thenApply(json -> {
                    CollectionType listType = objectMapper
                            .getTypeFactory()
                            .constructCollectionType(ArrayList.class, Map.class);
                    try {
                        List<Map<String, Object>> list = objectMapper.readValue(json, listType);

                        return list.stream()
                                .map(map -> (String) map.get("resource"))
                                .filter(res -> !Strings.isNullOrEmpty(res))
                                .anyMatch(uri -> Objects.equals(uri, resource));
                    } catch (JsonProcessingException e) {
                        log.error("[{}]权限json解析出现异常~", principal, e);
                        return false;
                    }
                });


    }
}
