package com.davies.naraka.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author davies
 * @date 2022/1/31 11:24 AM
 */

@ConfigurationProperties(prefix = "naraka.mybatis")
@Data
public class MyBatisProperties {

    private boolean dataPermission = false;

    private String fillCreatedTime ="createdTime";

    private String fillCreatedBy ="createdBy";

    private String fillUpdatedTime="updatedTime";

    private String fillUpdatedBy="updatedBy";

    private boolean metaObjectFill = false;




}
