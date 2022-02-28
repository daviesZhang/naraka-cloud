package com.davies.naraka.admin.common;

import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 把List<List<T>> 转为 Map<D,Set<V>>>
 *
 * @author davies
 * @date 2022/1/27 3:35 PM
 */
public class ListMapCollector<T, D, V> implements Collector<List<T>, Map<D, Set<V>>, Map<D, Set<V>>> {

    private final BiConsumer<Map<D, Set<V>>, List<T>> accumulator;

    public ListMapCollector(BiConsumer<Map<D, Set<V>>, List<T>> accumulator) {
        this.accumulator = accumulator;
    }

    @Override
    public Supplier<Map<D, Set<V>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<D, Set<V>>, List<T>> accumulator() {
        return this.accumulator;
    }

    @Override
    public BinaryOperator<Map<D, Set<V>>> combiner() {
        return (dSetMap, dSetMap2) -> Stream.of(dSetMap, dSetMap2)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> {
                    v1.addAll(v2);
                    return v1;
                }));
    }

    @Override
    public Function<Map<D, Set<V>>, Map<D, Set<V>>> finisher() {
        return stringListMap -> stringListMap;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Sets.newHashSet(Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
    }
}
