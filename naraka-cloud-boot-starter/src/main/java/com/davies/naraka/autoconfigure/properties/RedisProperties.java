package com.davies.naraka.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 其他可用配置参考
 * https://github.com/redisson/redisson/wiki/2.-Configuration
 * 后续根据项目需要添加
 * @author davies
 * @date 2022/2/28 3:03 PM
 */
@ConfigurationProperties("naraka.redis")
public class RedisProperties {

    private List<String> address = new ArrayList<>();

    private String password;




    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
