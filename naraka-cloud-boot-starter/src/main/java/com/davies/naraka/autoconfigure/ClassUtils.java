package com.davies.naraka.autoconfigure;


import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * @author davies
 * @date 2022/1/25 3:56 PM
 */
@Slf4j
public class ClassUtils {

    protected static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final String GET_STRING = "get";

    /**
     * 根据性能情况 后续可考虑换成其他bean转换器
     * 拷贝source 的值到target对象
     *
     * @param source 源对象
     * @param target 目标对象
     * @return target
     */
    public static <T, V> T copyObject(V source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }


    /**
     * @param object
     * @param field
     * @param <T>
     * @return
     */
    public static <T> Optional<Object> getFieldValueAndIgnoreError(T object, Field field) {
        try {
            return Optional.ofNullable(getFieldValue(object, field));
        } catch (Throwable e) {
            log.debug("动态获取对象属性值时,调用{}#{}的get方法抛出异常~", object.getClass(), field.getName(), e);
            return Optional.empty();
        }
    }


    public static <T> Object getFieldValue(T object, Field field) throws Throwable {
        String name = field.getName();
        Class<?> oClass = object.getClass();
        String methodName = GET_STRING + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name);
        MethodHandle handle = LOOKUP.findVirtual(oClass, methodName, MethodType.methodType(field.getType()));
        return handle.invoke(object);

    }
}



