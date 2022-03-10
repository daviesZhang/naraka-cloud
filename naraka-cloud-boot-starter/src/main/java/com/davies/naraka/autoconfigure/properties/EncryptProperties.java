package com.davies.naraka.autoconfigure.properties;

import com.google.common.base.Strings;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author davies
 * @date 2022/1/31 11:24 AM
 */

@ConfigurationProperties(prefix = "naraka.encrypt")
@Data
public class EncryptProperties {

    private final static String DEFAULT_KEY = "asdasf$!423523@@sdaSAz{}";


    private boolean enable = true;

    private String defaultKey;

    private Map<String, String> keyMap = new HashMap<>();

    public String getKey() {
        return getKey(null);
    }

    public String getKey(String name) {
        if (Strings.isNullOrEmpty(name)) {
            if (Strings.isNullOrEmpty(getDefaultKey())) {
                return DEFAULT_KEY;
            } else {
                return getDefaultKey();
            }
        } else {
            String value = keyMap.getOrDefault(name, getDefaultKey());
            if (Strings.isNullOrEmpty(value)) {
                return DEFAULT_KEY;
            }
            return value;
        }
    }

    public String getDefaultKey() {
        return Strings.isNullOrEmpty(defaultKey) ? DEFAULT_KEY : defaultKey;
    }
}
