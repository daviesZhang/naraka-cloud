package com.davies.naraka.autoconfigure;

import java.util.Objects;

/**
 * @author davies
 * @date 2022/2/27 10:53 AM
 */
public class SecurityHelper {

    private static final String USER_CACHE_PREFIX = "user:";
    private static final String USER_AUTHORITY_CACHE_PREFIX = USER_CACHE_PREFIX+"authority:";

    public static final String ROOT = "root";


    public static boolean isRoot(String username){
        return Objects.equals(username, ROOT);
    }

    public static String userCacheKey(String username){
        return USER_CACHE_PREFIX + username;
    }

    public static String userAuthorityCacheKey(String username){
        return USER_AUTHORITY_CACHE_PREFIX + username;
    }
}
