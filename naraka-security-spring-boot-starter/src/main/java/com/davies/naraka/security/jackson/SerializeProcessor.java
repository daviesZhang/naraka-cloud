package com.davies.naraka.security.jackson;

import java.util.function.Function;

/**
 * 序列化字段时,根据配置处理的方法
 * @author davies
 * @date 2022/2/10 6:55 PM
 */
public class SerializeProcessor  implements Function<String, String> {

    private final Function<String, String> previous;

    private final Function<String, String> current;

    public SerializeProcessor(Function<String, String> current, Function<String, String> previous) {
        this.previous = previous;
        this.current = current;
    }

    @Override
    public String apply(String s) {
        return current.apply(previous == null ? s : previous.apply(s));
    }

}
