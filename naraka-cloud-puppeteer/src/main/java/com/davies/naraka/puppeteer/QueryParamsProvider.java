package com.davies.naraka.puppeteer;

import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.cloud.common.StringConstants;
import com.davies.naraka.puppeteer.annotation.QueryConfig;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.Query;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static java.util.Locale.ENGLISH;

/**
 * @author davies
 * @date 2022/3/28 19:15
 */
@Slf4j
public class QueryParamsProvider {


    private static final String GET_STRING = "get";

    private static final String FIELD_TEMPLATE = "%s ";

    private static final String PARAMS_TEMPLATE = " :%s";

    private static final String SQL_WHERE = " WHERE ";

    private static final String SQL_AND = " AND ";

    public static final String SQL_ORDER_BY = " ORDER BY ";


    public static <T> QueryParams query(T o, Sort sort, QueryParamsInterceptor<T> interceptor) {
        return query(o, null, sort, interceptor);
    }

    public static <T> QueryParams query(T o, Sort sort) {
        return query(o, null, sort, null);
    }

    public static <T> QueryParams query(T o, QueryParamsInterceptor<T> interceptor) {
        return query(o, null, null, interceptor);
    }

    public static <T> QueryParams query(T o) {
        return query(o, null, null, null);
    }


    public static <T> QueryParams query(T o, Pageable pageable, QueryParamsInterceptor<T> interceptor) {
        return query(o, pageable, null, interceptor);
    }

    public static <T> QueryParams query(T o, Pageable pageable) {
        return query(o, pageable, null, null);
    }


    private static <T> QueryParams query(T o, Pageable pageable, Sort sort, QueryParamsInterceptor<T> interceptor) {
        StringBuilder builder = new StringBuilder();
        UnaryOperator<Query> queryConsumer;
        if (o instanceof Map) {
            queryConsumer = queryByMap((Map<Object, Object>) o, builder, UnaryOperator.identity(), interceptor);
        } else {
            queryConsumer = queryByObject(o, builder, UnaryOperator.identity(), interceptor);
        }
        boolean isNotBlank = builder.length() >= 0;
        if (null != interceptor) {
            String sql = interceptor.afterByWhere(o, builder.toString());
            if (!Strings.isNullOrEmpty(sql)) {
                if (isNotBlank) {
                    builder.append(SQL_AND);
                } else {
                    isNotBlank = true;
                }
                builder.append(sql);
            }
            queryConsumer = interceptor.queryConsumer(queryConsumer);
        }
        if (isNotBlank) {
            builder.insert(0, SQL_WHERE);
        }
        if (sort == null && pageable != null) {
            sort = pageable.getSort();
        }
        if (sort != null) {
            String sortString = sort.stream().map(s -> s.getProperty() + StringConstants.SPACE + s.getDirection().name())
                    .collect(Collectors.joining(StringConstants.COMMA));
            if (!Strings.isNullOrEmpty(sortString)) {
                builder.append(SQL_ORDER_BY).append(sortString);
            }
        }
        if (interceptor != null) {
            return interceptor.after(o, new QueryParams(builder.toString(), queryConsumer, pageable));
        }
        return new QueryParams(builder.toString(), queryConsumer, pageable);
    }

    private static <T> UnaryOperator<Query> queryByMap(Map<Object, Object> map, StringBuilder builder, UnaryOperator<Query> queryConsumer, QueryParamsInterceptor<T> interceptor) {

        if (map == null || map.isEmpty()) {
            return queryConsumer;
        }
        boolean isNotBlank = false;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (null != interceptor && !interceptor.support(key)) {
                continue;
            }
            if (value == null) {
                continue;
            }
            if (isNotBlank) {
                builder.append(SQL_AND);
            }
            if (!isNotBlank) {
                isNotBlank = true;
            }
            String paramsTemplate;
            if (value instanceof Collection) {
                paramsTemplate = filterType(QueryFilterType.CONTAINS);
            } else {
                paramsTemplate = filterType(QueryFilterType.EQUALS);
            }
            builder.append(Strings.lenientFormat(FIELD_TEMPLATE, key))
                    .append(Strings.lenientFormat(paramsTemplate, key));
            queryConsumer = (UnaryOperator<Query>) queryConsumer.andThen(((UnaryOperator<Query>) query -> query.setParameter(key, value)));
        }
        return queryConsumer;
    }

    private static <T> UnaryOperator<Query> queryByObject(T object, StringBuilder builder, UnaryOperator<Query> queryConsumer, QueryParamsInterceptor<T> interceptor) {
        if (object == null) {
            return queryConsumer;
        }
        boolean isNotBlank = false;
        Class<?> oClass = object.getClass();
        Field[] fields = oClass.getDeclaredFields();
        for (Field field : fields) {
            if (null != interceptor && !interceptor.support(field)) {
                continue;
            }
            QueryConfig queryConfig = field.getDeclaredAnnotation(QueryConfig.class);
            if (null != queryConfig && queryConfig.skip()) {
                continue;
            }
            String name = field.getName();
            String methodName = GET_STRING + capitalize(name);
            Object params;
            try {
                MethodHandle handle = MethodHandles.lookup().findVirtual(oClass, methodName, MethodType.methodType(field.getType()));
                params = handle.invoke(object);
            } catch (Throwable e) {
                log.warn("动态构造查询条件时,调用{}#{}方法抛出异常~", oClass.getName(), methodName, e);
                continue;
            }
            String alias = name;
            if (queryConfig != null && !Strings.isNullOrEmpty(queryConfig.alias())) {
                alias = queryConfig.alias();
            }
            if (params == null) {
                continue;
            }
            if (isNotBlank) {
                builder.append(SQL_AND);
            }
            if (!isNotBlank) {
                isNotBlank = true;
            }
            String paramsTemplate = filterType(queryConfig == null ? QueryFilterType.EQUALS : queryConfig.filterType());
            builder.append(Strings.lenientFormat(FIELD_TEMPLATE, alias))
                    .append(Strings.lenientFormat(paramsTemplate, name));
            queryConsumer = (UnaryOperator<Query>) queryConsumer.andThen(((UnaryOperator<Query>) query -> query.setParameter(name, params)));
        }
        return queryConsumer;
    }


    private static String filterType(QueryFilterType filterType) {
        switch (filterType) {
            case CONTAINS:
                return " in ( " + PARAMS_TEMPLATE + " )";
            case NOT_CONTAINS:
                return " not in ( " + PARAMS_TEMPLATE + " )";
            case GREATERTHANEQUAL:
                return " >= " + PARAMS_TEMPLATE;
            case EQUALS:
            default:
                return " = " + PARAMS_TEMPLATE;
        }

    }


    private static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }


}
