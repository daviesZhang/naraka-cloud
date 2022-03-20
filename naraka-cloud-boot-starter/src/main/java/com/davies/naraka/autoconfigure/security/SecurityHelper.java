package com.davies.naraka.autoconfigure.security;

import java.util.Objects;

/**
 * @author davies
 * @date 2022/2/27 10:53 AM
 */
public class SecurityHelper {

    private static final String USER_CACHE_PREFIX = "user:";
    private static final String USER_AUTHORITY_SERIALIZE_CACHE_PREFIX = ":authority:serialize";
    private static final String USER_AUTHORITY_ROW_CACHE_PREFIX = ":authority:row";
    private static final String USER_AUTHORITY_API_CACHE_PREFIX = ":authority:api";

    public static final String ROOT = "root";


    public static boolean isRoot(String username) {
        return Objects.equals(username, ROOT);
    }

    public static String userCacheKey(String username) {
        return USER_CACHE_PREFIX + username;
    }

    public static String userAuthoritySerializeCacheKey(String username) {
        return USER_CACHE_PREFIX + username + USER_AUTHORITY_SERIALIZE_CACHE_PREFIX;
    }


    public static String userAuthorityApiCacheKey(String username) {
        return USER_CACHE_PREFIX + username + USER_AUTHORITY_API_CACHE_PREFIX;
    }

    public static String userAuthorityRowCacheKey(String username) {
        return USER_CACHE_PREFIX + username + USER_AUTHORITY_ROW_CACHE_PREFIX;
    }
}
