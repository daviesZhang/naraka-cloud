package com.davies.naraka.autoconfigure.security;

import java.util.Objects;

/**
 * @author davies
 * @date 2022/2/27 10:53 AM
 */
public class SecurityHelper {

    private static final String USER_CACHE_PREFIX = "user:";
    private static final String USER_AUTHORITY_SERIALIZE_CACHE_PREFIX = USER_CACHE_PREFIX + "authority:serialize";
    private static final String USER_AUTHORITY_DATA_CACHE_PREFIX = USER_CACHE_PREFIX + "authority:data";
    private static final String USER_AUTHORITY_API_CACHE_PREFIX = USER_CACHE_PREFIX + "authority:api";

    public static final String ROOT = "root";


    public static boolean isRoot(String username) {
        return Objects.equals(username, ROOT);
    }

    public static String userCacheKey(String username) {
        return USER_CACHE_PREFIX + username;
    }

    public static String userAuthoritySerializeCacheKey(String username) {
        return USER_AUTHORITY_SERIALIZE_CACHE_PREFIX + username;
    }


    public static String userAuthorityApiCacheKey(String username) {
        return USER_AUTHORITY_API_CACHE_PREFIX + username;
    }

    public static String userAuthorityDataCacheKey(String username) {
        return USER_AUTHORITY_DATA_CACHE_PREFIX + username;
    }
}
