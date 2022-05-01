package com.davies.naraka.autoconfigure;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * @author davies
 * @date 2022/2/28 4:11 PM
 */
public interface HasResources extends BiFunction<String, String, CompletableFuture<Boolean>> {


}
