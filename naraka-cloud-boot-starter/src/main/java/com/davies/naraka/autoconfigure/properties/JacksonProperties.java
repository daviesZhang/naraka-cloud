package com.davies.naraka.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author davies
 */
@ConfigurationProperties(prefix = "naraka.jackson")
@Data
public class JacksonProperties {

    /**
     * 是否启用序列化时处理字段 过滤,脱敏等其他操作
     */
    private boolean serializerCustomField;
    /**
     * 是否生成一个单例默认ObjectMapper
     */
    private boolean objectMapper;
    /**
     * 是否生成一个MappingJacksonHttpMessageConverter
     */
    private boolean messageConverter;

    /**
     * 是否在反序列化时智能转换QueryField对象
     * @see com.davies.naraka.autoconfigure.domain.QueryField
     */
    private boolean deserializerSmart;
}
