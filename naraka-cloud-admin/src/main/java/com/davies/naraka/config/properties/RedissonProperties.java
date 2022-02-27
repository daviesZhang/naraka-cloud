package com.davies.naraka.config.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 其他可用配置参考
 * https://github.com/redisson/redisson/wiki/2.-Configuration
 * 后续根据项目需要添加
 * @author davies
 * @date 2022/2/21 10:23 AM
 */
@Data
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = "app.redisson")
public class RedissonProperties {

    private List<String> address = new ArrayList<>();

    private String password;


}
