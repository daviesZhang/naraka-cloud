package com.davies.naraka.autoconfigure;



import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 根据resource获取权限配置,比如字段过滤等等
 *
 * @author davies
 * @date 2022/2/27 8:03 PM
 */
public interface ProcessorFunction extends Function<String, Map<String, Set<String>>> {
}
