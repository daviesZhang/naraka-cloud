package com.davies.naraka.autoconfigure.jackson;

import java.util.function.Function;

/**
 * 在jackson序列化时,对内容进行修改
 *
 * @author davies
 * @date 2022/3/4 1:46 PM
 */
public interface SerializeBeanPropertyFunction extends Function<Object, Object> {


}
