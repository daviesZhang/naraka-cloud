package com.davies.naraka.autoconfigure.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.QueryPage;
import com.davies.naraka.autoconfigure.QueryUtils;
import com.davies.naraka.autoconfigure.annotation.ColumnName;
import com.davies.naraka.autoconfigure.annotation.QueryConfig;
import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author davies
 * @date 2022/1/30 2:27 PM
 */

public class MyBatisQueryUtils extends QueryUtils {


    @Getter
    @Setter
    private List<MyBatisQueryHandle> myBatisQueryHandleList;

    public MyBatisQueryUtils() {

    }

    public MyBatisQueryUtils(List<MyBatisQueryHandle> myBatisQueryHandleList) {
        this.myBatisQueryHandleList = myBatisQueryHandleList;
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
        return pageQuery(supplier, buildQueryWrapper(new QueryWrapper<>(), query.getQuery()),
                Page.of(query.getCurrent(), query.getSize()), service);
    }


    public <T, E, Q, R> PageDTO<T> pageQuery(Supplier<T> supplier, QueryPage<Q> query, BiFunction<Page<R>, QueryWrapper<E>, Page<R>> pageFunction) {
        Page<R> page = Page.of(query.getCurrent(), query.getSize());
        return pageQuery(supplier, buildQueryWrapper(new QueryWrapper<>(), query.getQuery()),
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
        return new PageDTO<>((int) page.getCurrent(), (int) page.getTotal(), (int) page.getSize(), items);
    }

    public <T, E, R> PageDTO<T> pageQuery(Supplier<T> supplier, QueryWrapper<E> queryWrapper, Page<R> page, BiFunction<Page<R>, QueryWrapper<E>, Page<R>> pageFunction) {
        pageFunction.apply(page, queryWrapper);
        List<T> items = page.getRecords().stream().map(item -> ClassUtils.copyObject(item, supplier.get())).collect(Collectors.toList());
        return new PageDTO<>((int) page.getCurrent(), (int) page.getTotal(), (int) page.getSize(), items);
    }

    /**
     * @param queryWrapper QueryWrapper 对象
     * @param query        查询的类
     * @param <T>          entity
     * @return QueryWrapper<entity>
     */
    public <T> QueryWrapper<T> buildQueryWrapper(@NotNull QueryWrapper<T> queryWrapper, @NotNull Object query) {
        Class<?> classes = query.getClass();

        if (query instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) query).entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    buildQueryParams(queryWrapper, value, converterToColumn(key.toString()), null);
                }
            }
            return queryWrapper;
        }
        Field[] fields = classes.getDeclaredFields();
        for (Field field : fields) {
            Optional<Object> optional = queryValue(query, field);
            if (!optional.isPresent()) {
                continue;
            }
            Object value = optional.get();
            ColumnName columnName = field.getDeclaredAnnotation(ColumnName.class);
            String column;
            if (columnName != null && !Strings.isNullOrEmpty(columnName.value())) {
                column = columnName.value();
            } else {
                column = converterToColumn(field.getName());
            }
            buildQueryParams(queryWrapper, value, column, field);
        }
        return queryWrapper;
    }

    private <T> void buildQueryParams(@NotNull QueryWrapper<T> queryWrapper,
                                      @NotNull Object value,
                                      @NotNull String column,
                                      Field field) {
        if (value instanceof Collection) {
            Collection<?> items = (Collection<?>) value;
            if (items.isEmpty()) {
                return;
            }
            Object firstItem = items.iterator().next();
            if (firstItem instanceof QueryField) {
                for (Object item : items) {
                    buildQueryWrapper(queryWrapper, column, (QueryField<?>) item, field);
                }
            } else {
                buildQueryField(items, true, field)
                        .ifPresent(queryField -> buildQueryWrapper(queryWrapper, column, queryField, field));
            }
        } else if (value instanceof QueryField) {
            buildQueryWrapper(queryWrapper, column, (QueryField<?>) value, field);
        } else {
            buildQueryField(value, false, field)
                    .ifPresent(queryField -> buildQueryWrapper(queryWrapper, column, queryField, field));
        }
    }


    private Optional<QueryField<?>> buildQueryField(Object value, boolean isCollection, Field field) {
        QueryConfig config = null;
        if (field != null) {
            config = field.getDeclaredAnnotation(QueryConfig.class);
        }
        QueryField<?> queryField;
        if (config == null) {
            if (isCollection) {
                queryField = new QueryField<Collection<?>>(QueryFilterType.CONTAINS, (Collection<?>) value);
            } else {
                queryField = new QueryField<>(QueryFilterType.EQ, value);
            }
        } else {
            queryField = new QueryField<>(config.filterType(), value);

        }
        return Optional.ofNullable(queryField);

    }


    /**
     * 多字段排序时不支持指定顺序
     *
     * @param queryWrapper
     * @param column
     * @param queryField
     * @param field
     * @param <T>
     */
    private <T> void buildQueryWrapper(QueryWrapper<T> queryWrapper, String column, QueryField<?> queryField, Field field) {
        if (field != null) {
            checkFilterType(queryField, field);
        }
        Object value = queryField.getFilter();
        QueryFilterType filterType = queryField.getType();

        List<MyBatisQueryHandle> handleList = getMyBatisQueryHandleList();
        List<MyBatisQueryHandle> list;
        if (handleList == null || handleList.isEmpty()) {
            list = Lists.newArrayList();
        } else {
            list = Lists.newArrayList(handleList);
        }
        list.add(new DefaultMyBatisQueryHandle());
        for (MyBatisQueryHandle myBatisQueryHandle : list) {
            boolean support = myBatisQueryHandle.support(filterType, column, value, field);
            if (support) {
                myBatisQueryHandle.handle(queryWrapper, filterType, column, value, field);
                return;
            }
        }
        throw new IllegalArgumentException(Strings.lenientFormat("%s 不支持这个运算符", filterType));
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
