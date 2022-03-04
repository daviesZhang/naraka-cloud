package com.davies.naraka.autoconfigure.jackson;

import java.util.function.Function;

/**
 * @author davies
 * @date 2022/3/4 2:00 PM
 */
public interface SerializeBeanPropertyFactory extends Function<String, SerializeBeanPropertyFunction> {


    String SERIALIZE_PREFIX = "serialize";


    /**
     * 当当key为serializeSkip时,直接跳过序列化,不处理
     */
    String SERIALIZE_SKIP = SERIALIZE_PREFIX + "Filter";


}
