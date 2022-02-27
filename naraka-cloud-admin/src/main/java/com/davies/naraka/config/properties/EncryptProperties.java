package com.davies.naraka.config.properties;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author davies
 * @date 2022/1/31 11:24 AM
 */
@Data
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = "app.data.encrypt")
public class EncryptProperties {

    private final static String DEFAULT_KEY = "asdasf$!423523@@sdaSAz{}";

    private String defaultKey;

    private Map<String, String> keyMap = new HashMap<>();


    public String getKey(){
        return getKey(null);
    }

    public String getKey(String name){
        if (Strings.isNullOrEmpty(name)) {
            if (Strings.isNullOrEmpty(getDefaultKey())){
                return DEFAULT_KEY;
            }else{
                return getDefaultKey();
            }
        }else{
            String value = keyMap.getOrDefault(name, getDefaultKey());
            if (Strings.isNullOrEmpty(value)){
                return DEFAULT_KEY;
            }
            return value;
        }
    }
}
