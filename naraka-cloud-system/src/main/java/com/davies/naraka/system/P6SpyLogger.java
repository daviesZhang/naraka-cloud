package com.davies.naraka.system;

import com.google.common.base.Strings;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

/**
 * @author davies
 * @date 2022/6/1 17:42
 */
public class P6SpyLogger implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category,
                                String prepared, String sql, String url) {
        return !Strings.isNullOrEmpty(sql) ? " Consume Time：" + elapsed + " ms " + now +
                "\n Execute SQL：" + sql.replaceAll("[\\s]+", " ") + "\n" : "";
    }
}
