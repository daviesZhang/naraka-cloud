package com.davies.naraka.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author davies
 * @date 2022/2/27 6:55 PM
 */
@ConfigurationProperties("naraka.security")
public class SecurityProperties {

    /**
     * 是否启用SecurityAutoConfiguration
     */
    private boolean enable = true;





    /**
     * token过期时间,分钟
     */
    private int expiresAt=30;

    private String jwtSecret = "dsafsd@#!55DAXF&";






    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }




    public int getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(int expiresAt) {
        this.expiresAt = expiresAt;
    }
}
