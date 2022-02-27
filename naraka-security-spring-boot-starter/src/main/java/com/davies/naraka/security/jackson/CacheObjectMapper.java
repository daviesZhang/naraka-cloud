package com.davies.naraka.security.jackson;


import com.davies.naraka.security.CurrentUserNameSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 为每个用户缓存CacheObjectMapper对象
 * 避免每次请求都创建新的ObjectMapper
 * 这意味,当修改了用户的接口字段权限,需要缓存刷新后生效
 * 一般系统来说无需及时刷新,所以不做手动刷新的调用策略
 * @author davies
 * @date 2022/1/29 8:31 PM
 */
public class CacheObjectMapper implements Supplier<ObjectMapper> {

    private final Supplier<ObjectMapper> supplier;
    private final static Cache<String, ObjectMapper> OBJECT_MAPPER_CACHE = CacheBuilder
            .newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();


    private final CurrentUserNameSupplier currentUserNameSupplier;
    private ObjectMapper defaultObjectMapper = null;

    public CacheObjectMapper(Supplier<ObjectMapper> supplier, CurrentUserNameSupplier currentUserNameSupplier) {
        this.currentUserNameSupplier = currentUserNameSupplier;
        this.supplier = supplier;
    }

    @Override
    public ObjectMapper get() {
       String username = currentUserNameSupplier.get();
        if (!Strings.isNullOrEmpty(username)) {
            try {
                return OBJECT_MAPPER_CACHE.get(username, supplier::get);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return getDefaultObjectMapper();
    }

    public ObjectMapper getDefaultObjectMapper() {
        return defaultObjectMapper;
    }

    public void setDefaultObjectMapper(ObjectMapper defaultObjectMapper) {
        this.defaultObjectMapper = defaultObjectMapper;
    }
}
