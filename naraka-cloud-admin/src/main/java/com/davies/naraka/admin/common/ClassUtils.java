package com.davies.naraka.admin.common;


import org.springframework.beans.BeanUtils;

/**
 * @author davies
 * @date 2022/1/25 3:56 PM
 */
public class ClassUtils {


    /**
     * 拷贝source 的值到target对象
     * @param source 源对象
     * @param target 目标对象
     * @return target
     */
    public static <T,V> T copyObject(V source,T target){
        BeanUtils.copyProperties(source,target);
        return target;
    }
}



