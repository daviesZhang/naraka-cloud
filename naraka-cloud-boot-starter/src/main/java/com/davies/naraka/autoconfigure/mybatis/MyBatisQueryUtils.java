package com.davies.naraka.autoconfigure.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.annotation.ColumnName;
import com.davies.naraka.autoconfigure.annotation.Crypto;
import com.davies.naraka.autoconfigure.annotation.QueryFilter;
import com.davies.naraka.autoconfigure.annotation.QuerySkip;
import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.autoconfigure.properties.EncryptProperties;
import com.davies.naraka.cloud.common.AesEncryptorUtils;
import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Locale.ENGLISH;

/**
 * @author davies
 * @date 2022/1/30 2:27 PM
 */

public class MyBatisQueryUtils {

    private static final String READ_METHOD = "get";

    private final EncryptProperties encryptProperties;

    public MyBatisQueryUtils(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }

    private static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }


    /**
     * @param supplier<T> 数据列表的DTO类实例
     * @param query       查询条件包装
     * @param service     分页查询服务接口
     * @param <T>         DTO
     * @param <E>         ENTITY
     * @param <Q>         QueryDTO
     * @return PageDTO
     */
    public <T, E, Q> PageDTO<T> pageQuery(Supplier<T> supplier, QueryPage<Q> query, IService<E> service) {
        return pageQuery(supplier, buildQueryWrapper(new QueryWrapper<E>(), query.getQuery()),
                Page.of(query.getCurrent(), query.getSize()), service);
    }

    public <T, E, Q, R> PageDTO<T> pageQuery(Supplier<T> supplier, QueryPage<Q> query, BiFunction<Page<R>, QueryWrapper<E>, Page<R>> pageFunction) {
        Page<R> page = Page.of(query.getCurrent(), query.getSize());
        return pageQuery(supplier, buildQueryWrapper(new QueryWrapper<E>(), query.getQuery()),
                page, pageFunction);
    }

    /**
     * @param supplier<T>  数据列表的DTO类实例
     * @param queryWrapper 查询条件包装
     * @param page         分页信息
     * @param service      分页查询服务接口
     * @param <T>          DTO
     * @param <E>          ENTITY
     * @return PageDTO
     */
    public <T, E> PageDTO<T> pageQuery(Supplier<T> supplier, QueryWrapper<E> queryWrapper, Page<E> page, IService<E> service) {
        service.page(page, queryWrapper);
        List<T> items = page.getRecords().stream().map(item -> ClassUtils.copyObject(item, supplier.get())).collect(Collectors.toList());
        return new PageDTO<T>(page.getCurrent(), page.getTotal(), page.getSize(), items);
    }

    public <T, E, R> PageDTO<T> pageQuery(Supplier<T> supplier, QueryWrapper<E> queryWrapper, Page<R> page, BiFunction<Page<R>, QueryWrapper<E>, Page<R>> pageFunction) {
        pageFunction.apply(page, queryWrapper);
        List<T> items = page.getRecords().stream().map(item -> ClassUtils.copyObject(item, supplier.get())).collect(Collectors.toList());
        return new PageDTO<T>(page.getCurrent(), page.getTotal(), page.getSize(), items);
    }

    /**
     * @param queryWrapper      QueryWrapper 对象
     * @param query             查询的类
     * @param converterToColumn 查询条件 field name转换为数据库column name 方法
     *                          第一个参数 字段名 第二个参数 如果ColumnName注解,则使用ColumnName name,否则也是字段名
     * @param <T>               entity
     * @return QueryWrapper<entity>
     */
    public <T> QueryWrapper<T> buildQueryWrapper(@NotNull QueryWrapper<T> queryWrapper, @NotNull Object query,
                                                 BiFunction<String, String, String> converterToColumn) {

        Class<?> classes = query.getClass();
        Field[] fields = classes.getDeclaredFields();
        for (Field declaredField : fields) {
            if (declaredField.isAnnotationPresent(QuerySkip.class)) {
                continue;
            }
            String name = declaredField.getName();
            Object field;
            try {
                Method method = BeanUtils.findDeclaredMethodWithMinimalParameters(classes, READ_METHOD + capitalize(name));
                if (null == method) {
                    continue;
                }
                field = method.invoke(query);
            } catch (IllegalArgumentException | IllegalAccessException |
                    InvocationTargetException e) {
                continue;
            }
            String column = name;
            ColumnName columnName = declaredField.getDeclaredAnnotation(ColumnName.class);
            if (columnName != null && !Strings.isNullOrEmpty(columnName.name())) {
                column = columnName.name();
            }
            if (converterToColumn != null) {
                column = converterToColumn.apply(name, column);
            }
            String key = getEncryptKey(declaredField);
            if (field instanceof List) {
                List<?> items = (List<?>) field;
                if (items.isEmpty()) {
                    continue;
                }
                Object firstItem = items.get(0);
                if (firstItem instanceof QueryField) {
                    for (Object item : items) {
                        buildQueryField(queryWrapper, column, (QueryField<?>) item, declaredField);
                    }
                } else {
                    if (Strings.isNullOrEmpty(key)) {
                        queryWrapper.in(column, items);
                    } else {
                        List<String> values = items.stream().map(v -> encrypt((String) v, key)).collect(Collectors.toList());
                        queryWrapper.in(column, values);
                    }
                }
            } else if (field instanceof QueryField) {
                buildQueryField(queryWrapper, column, (QueryField<?>) field, declaredField);
            } else if (field != null) {
                if (Strings.isNullOrEmpty(key)) {
                    queryWrapper.eq(column, field);
                } else {
                    queryWrapper.eq(column, encrypt((String) field, key));
                }
            }
        }
        return queryWrapper;
    }


    private String getEncryptKey(Field field) {
        if (!encryptProperties.isEnable()) {
            return null;
        }
        Crypto crypto = field.getDeclaredAnnotation(Crypto.class);
        String key = null;
        if (crypto != null) {
            key = encryptProperties.getKey(Strings.isNullOrEmpty(crypto.name()) ? field.getName() : crypto.name());
        }
        return key;
    }

    private String encrypt(String object, String key) {

        try {
            return AesEncryptorUtils.encrypt(object, key);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(  "加密异常", e);
        }

    }


    public <T> QueryWrapper<T> buildQueryWrapper(@NotNull QueryWrapper<T> queryWrapper, @NotNull Object query,
                                                 Map<String, String> columnMap) {
        boolean isNullOrEmpty = columnMap == null || columnMap.isEmpty();
        return buildQueryWrapper(queryWrapper, query, isNullOrEmpty ? (name, column) -> converterToColumn(column) : (name, column) -> {
            String value = columnMap.get(name);
            if (Strings.isNullOrEmpty(column)) {
                return column;
            }
            return value;
        });
    }


    /**
     * 字段名称映射默认使用 passwordExpireTime => password_expire_time
     *
     * @param queryWrapper 查询包装器
     * @param query        查询条件
     * @param <T>          entity
     * @return
     */
    public <T> QueryWrapper<T> buildQueryWrapper(@NotNull QueryWrapper<T> queryWrapper, @NotNull Object query) {
        return buildQueryWrapper(queryWrapper, query, (name, column) -> converterToColumn(column));
    }


    /**
     * 数据库解密
     *
     * @param column
     * @param key
     * @return
     */
    private String dbDecryptColumn(String column, String key) {
        return "AES_DECRYPT(UNHEX(" + column + "),'" + key + "')";
    }

    /**
     * 多字段排序时不支持指定顺序
     *
     * @param queryWrapper
     * @param column
     * @param queryField
     * @param declaredField
     * @param <T>
     */
    private <T> void buildQueryField(QueryWrapper<T> queryWrapper, String column, QueryField<?> queryField, Field declaredField) {
        QueryFilter queryFilter = declaredField.getAnnotation(QueryFilter.class);
        QueryFilterType filterType = queryField.getType();
        if (queryFilter != null) {
            QueryFilterType[] types = queryFilter.types();
            boolean supportType = Arrays.stream(types).anyMatch(type -> type == queryField.getType());
            Preconditions.checkArgument(supportType, "filterType not support");
        }
        Object value = queryField.getFilter();
        String key = getEncryptKey(declaredField);
        switch (filterType) {
            case EQUALS:
                if (Strings.isNullOrEmpty(key)) {
                    queryWrapper.eq(column, value);
                } else {
                    queryWrapper.eq(column, encrypt((String) value, key));
                }
                break;
            case NOT_EQUALS:
                if (Strings.isNullOrEmpty(key)) {
                    queryWrapper.ne(column, value);
                } else {
                    queryWrapper.ne(column, encrypt((String) value, key));
                }
                break;
            case LIKE:
                if (Strings.isNullOrEmpty(key)) {
                    queryWrapper.like(column, value);
                } else {
                    queryWrapper.like(dbDecryptColumn(column, key), value);
                }
                break;
            case STARTS_WITH:
                if (Strings.isNullOrEmpty(key)) {
                    queryWrapper.likeRight(column, value);
                } else {
                    queryWrapper.likeRight(dbDecryptColumn(column, key), value);
                }
                break;
            case ENDS_WITH:
                if (Strings.isNullOrEmpty(key)) {
                    queryWrapper.likeLeft(column, value);
                } else {
                    queryWrapper.likeLeft(dbDecryptColumn(column, key), value);
                }
                break;
            case CONTAINS:
                this.queryListCondition(key, column, value, queryWrapper::in);
                break;
            case NOT_CONTAINS:
                this.queryListCondition(key, column, value, queryWrapper::notIn);
                break;
            case LESSTHAN:
                queryWrapper.lt(column, value);
                break;
            case GREATERTHANE:
                queryWrapper.gt(column, value);
                break;
            case LESSTHANEQUAL:
                queryWrapper.le(column, value);
                break;
            case GREATERTHANEQUAL:
                queryWrapper.ge(column, value);
                break;
            case ORDER_ASC:
                queryWrapper.orderByAsc(column);
                break;
            case ORDER_DESC:
                queryWrapper.orderByDesc(column);
                break;
            default:
                throw new IllegalArgumentException(Strings.lenientFormat("%s 不支持这个运算符", filterType));
        }
    }

    private void queryListCondition(String key, String column, Object value, BiConsumer<String, List<?>> biFunction) {
        if (Strings.isNullOrEmpty(key)) {
            if (value instanceof Collection) {
                Collection<?> values = (Collection<?>) value;
                if (!values.isEmpty()) {
                    biFunction.accept(column, Lists.newArrayList(values));
                }
            } else {
                biFunction.accept(column, Lists.newArrayList(value));
            }
        } else {
            if (value instanceof List) {
                List<String> values = ((List<?>) value)
                        .stream()
                        .map(v -> encrypt((String) v, key))
                        .collect(Collectors.toList());
                if (!values.isEmpty()) {
                    biFunction.accept(column, values);
                }
            } else {
                biFunction.accept(column, Lists.newArrayList(encrypt((String) value, key)));
            }
        }
    }

    /**
     * passwordExpireTime => password_expire_time
     *
     * @param value 初始值
     * @return LOWER_UNDERSCORE
     */
    private String converterToColumn(@NotNull String value) {
        return CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(value);
    }
}
