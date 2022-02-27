package com.davies.naraka.security.properties;

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
     * 启用RemoteUserFilter 过滤器,把header authorization的内容作为remoteUser
     */
    private boolean remoteUser = false;

    /**
     * token过期时间,分钟
     */
    private int expiresAt=30;

    private String jwtSecret = "dsafsd@#!55DAXF&";

    private String dateTimePattern = "yyyy-MM-dd HH:mm:ss";


    /**
     * 是否需要 自动注入bean jackson ObjectMapper
     */
    private boolean jackson;

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


    public String getDateTimePattern() {
        return dateTimePattern;
    }

    public void setDateTimePattern(String dateTimePattern) {
        this.dateTimePattern = dateTimePattern;
    }

    public boolean isRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(boolean remoteUser) {
        this.remoteUser = remoteUser;
    }

    public int getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(int expiresAt) {
        this.expiresAt = expiresAt;
    }
}
