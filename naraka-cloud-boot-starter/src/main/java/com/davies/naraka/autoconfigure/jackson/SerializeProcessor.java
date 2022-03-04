package com.davies.naraka.autoconfigure.jackson;

import java.util.function.Function;

/**
 * 序列化字段时,根据配置处理的方法
 *
 * @author davies
 * @date 2022/2/10 6:55 PM
 */
public class SerializeProcessor implements Function<Object, Object> {

    private final SerializeProcessor previous;

    private final SerializeBeanPropertyFunction current;

    public SerializeProcessor(SerializeBeanPropertyFunction current, SerializeProcessor previous) {
        this.previous = previous;
        this.current = current;
    }

    @Override
    public Object apply(Object s) {
        return current.apply(previous == null ? s : previous.apply(s));
    }

}
