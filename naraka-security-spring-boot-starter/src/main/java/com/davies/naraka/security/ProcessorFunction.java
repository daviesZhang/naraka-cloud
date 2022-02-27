package com.davies.naraka.security;

import com.davies.naraka.cloud.common.enums.AuthorityProcessorType;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author davies
 * @date 2022/2/27 8:03 PM
 */
public interface ProcessorFunction extends Function<String, Map<String, Set<AuthorityProcessorType>>> {
}
