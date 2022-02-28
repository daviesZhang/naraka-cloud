package com.davies.naraka.admin.common;

import com.davies.naraka.admin.domain.enums.AuthorityProcessorType;

import java.util.*;

/**
 * @author davies
 * @date 2022/2/27 10:21 AM
 */
public class SecurityUtils {

    public static final String ROOT = "root";

    private static final ThreadLocal<String> USERNAME_THREAD_LOCAL = new ThreadLocal<>();


    public static boolean isRoot(String username){

        return Objects.equals(username, ROOT);
    }

    public static Map<String, Set<AuthorityProcessorType>> getProcessor(String key){

        return new HashMap<>();
    }

    public static Optional<String> currentUsername(){

        return Optional.ofNullable(USERNAME_THREAD_LOCAL.get());
    }


    public static void clearUser(){
        USERNAME_THREAD_LOCAL.remove();

    }



}
