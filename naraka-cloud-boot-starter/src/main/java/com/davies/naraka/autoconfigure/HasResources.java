package com.davies.naraka.autoconfigure;

import java.util.concurrent.CompletableFuture;

/**
 * @author davies
 * @date 2022/2/28 4:11 PM
 */
public interface HasResources {

    /**
     * 验证指定 主体 是否拥有这个资源
     * @param resource 资源
     * @param principal 主体,username
     * @return true false
     */
    CompletableFuture<Boolean> test(String resource, String principal);
}
