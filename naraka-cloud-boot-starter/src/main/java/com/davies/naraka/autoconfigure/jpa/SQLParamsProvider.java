package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.QueryPage;
import com.davies.naraka.autoconfigure.annotation.ColumnName;
import com.davies.naraka.autoconfigure.annotation.QueryConfig;
import com.davies.naraka.autoconfigure.annotation.QuerySkip;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.cloud.common.StringConstants;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;

import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author davies
 * @date 2022/3/28 19:15
 */
@Slf4j
public class SQLParamsProvider {


    private static final String FIELD_TEMPLATE = "%s ";

    private static final String PARAMS_TEMPLATE = " :%s";

    private static final String SQL_WHERE = " WHERE ";

    private static final String SQL_SET = " SET ";

    private static final String SQL_AND = " AND ";

    private static final String SQL_JOIN = " , ";

    private static final String SQL_ORDER_BY = " ORDER BY ";

    private static final String UPDATE_PARAMS_PRDFIX = "s_";


    public static <T> QuerySQLParams query(T o, Sort sort, QueryParamsInterceptor<T> interceptor) {
        return query(o, null, sort, interceptor);
    }

    public static <T> QuerySQLParams query(T o, Sort sort) {
        return query(o, null, sort, null);
    }

    public static <T> QuerySQLParams query(T o, QueryParamsInterceptor<T> interceptor) {
        return query(o, null, null, interceptor);
    }

    public static <T> QuerySQLParams query(T o) {
        return query(o, null, null, null);
    }


    public static <T> QuerySQLParams query(T o, Pageable pageable, QueryParamsInterceptor<T> interceptor) {
        return query(o, pageable, null, interceptor);
    }

    public static <T> QuerySQLParams query(T o, Pageable pageable) {
        return query(o, pageable, null, null);
    }


    public static <T> SQLParams update(T o) {
        return builder(o, false, null, null, null);
    }

    public static <T> SQLParams update(T o, QueryParamsInterceptor<T> interceptor) {
        return builder(o, false, null, null, interceptor);
    }


    private static <T> QuerySQLParams query(T o, Pageable pageable, Sort sort, QueryParamsInterceptor<T> interceptor) {
        return SQLParamsProvider.builder(o, true, pageable, sort, interceptor);
    }

    @SuppressWarnings({"unchecked"})
    private static <T, R extends SQLParams> R builder(T o, boolean isQueryParams, Pageable pageable, Sort sort, QueryParamsInterceptor<T> interceptor) {
        StringBuilder builder = new StringBuilder();
        Function<Query, Query> queryConsumer;

        List<Sort> sorts = new ArrayList<>();
        if (sort != null) {
            sorts.add(sort);
        }
        if (o instanceof Map) {
            queryConsumer = queryByMap((Map<Object, Object>) o, isQueryParams, builder, Function.identity(), interceptor);
        } else {
            if (o instanceof QueryPage) {
                if (pageable == null) {
                    pageable = PageRequest.of(((QueryPage<?>) o).getCurrent(), ((QueryPage<?>) o).getSize());
                }
                queryConsumer = queryByObject(((QueryPage<?>) o).getQuery(), isQueryParams, builder, Function.identity(), sorts, interceptor);
            } else {
                queryConsumer = queryByObject(o, isQueryParams, builder, Function.identity(), sorts, interceptor);
            }
        }
        String join = isQueryParams ? SQL_AND : SQL_JOIN;

        String prefix = isQueryParams ? SQL_WHERE : SQL_SET;

        if (null != interceptor) {
            String sql = interceptor.afterByWhere(o, builder.toString());
            if (!Strings.isNullOrEmpty(sql)) {
                if (builder.length() > 0) {
                    builder.append(join);
                }
                builder.append(sql);
            }
            queryConsumer = queryConsumer.andThen(query -> {
                interceptor.queryConsumer(o, query);
                return query;
            });
        }
        if (builder.length() > 0) {
            builder.insert(0, prefix);
        }

        if (sorts.isEmpty() && pageable != null) {
            sorts.add(pageable.getSort());
        }
        if (!sorts.isEmpty()) {
            String sortString = sorts.stream().flatMap(Streamable::get).map(s -> s.getProperty() + StringConstants.SPACE + s.getDirection().name())
                    .collect(Collectors.joining(StringConstants.COMMA));
            if (!Strings.isNullOrEmpty(sortString)) {
                builder.append(SQL_ORDER_BY).append(sortString);
            }
        }

        if (interceptor != null) {
            return (R) interceptor.after(o, new QuerySQLParams(builder.toString(), queryConsumer, pageable));
        }
        return (R) new QuerySQLParams(builder.toString(), queryConsumer, pageable);
    }


    private static <T> Function<Query, Query> queryByMap(Map<Object, Object> map,
                                                         boolean isQueryParams,
                                                         StringBuilder builder,
                                                         Function<Query, Query> queryConsumer,
                                                         QueryParamsInterceptor<T> interceptor) {
        if (map == null || map.isEmpty()) {
            return queryConsumer;
        }
        boolean isNotBlank = false;

        String join = isQueryParams ? SQL_AND : SQL_JOIN;

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
                builder.append(join);
            }
            if (!isNotBlank) {
                isNotBlank = true;
            }
            String paramsTemplate;
            if (value instanceof Collection) {
                paramsTemplate = filterType(QueryFilterType.CONTAINS);
            } else {
                paramsTemplate = filterType(QueryFilterType.EQ);
            }
            String paramsName = key;
            if (!isQueryParams) {
                paramsName = UPDATE_PARAMS_PRDFIX + key;
            }
            builder.append(Strings.lenientFormat(FIELD_TEMPLATE, key))
                    .append(Strings.lenientFormat(paramsTemplate, paramsName));
            String finalParamsName = paramsName;
            queryConsumer = queryConsumer.andThen(query -> query.setParameter(finalParamsName, value));
        }
        return queryConsumer;
    }

    private static <T, V> Function<Query, Query> queryByObject(T object,
                                                               boolean isQueryParams,
                                                               StringBuilder builder,
                                                               Function<Query, Query> queryConsumer,
                                                               List<Sort> sorts,
                                                               QueryParamsInterceptor<V> interceptor) {
        if (object == null) {
            return queryConsumer;
        }
        boolean isNotBlank = false;
        Class<?> oClass = object.getClass();
        Field[] fields = oClass.getDeclaredFields();
        String join = isQueryParams ? SQL_AND : SQL_JOIN;
        for (Field field : fields) {
            if (null != interceptor && !interceptor.support(field)) {
                continue;
            }

            if (null != field.getDeclaredAnnotation(QuerySkip.class)) {
                continue;
            }
            String name = field.getName();
            Optional<Object> valueOptional = ClassUtils.getFieldValueAndIgnoreError(object, field);
            if (!valueOptional.isPresent()) {
                continue;
            }
            Object params = valueOptional.get();
            String alias = name;
            ColumnName columnName = field.getDeclaredAnnotation(ColumnName.class);
            if (columnName != null && !Strings.isNullOrEmpty(columnName.value())) {
                alias = columnName.value();
            }
            String paramsName = name;
            if (!isQueryParams) {
                paramsName = UPDATE_PARAMS_PRDFIX + name;
            }

            QueryConfig queryConfig = field.getDeclaredAnnotation(QueryConfig.class);
            List<QueryField<?>> queryList = buildQueryField(queryConfig, params);
            for (QueryField<?> queryField : queryList) {

                QueryFilterType filterType = queryField.getType();
                Object value = queryField.getFilter();
                if (filterType == QueryFilterType.ASC) {
                    sorts.add(Sort.by(Sort.Direction.ASC, alias));
                } else if (filterType == QueryFilterType.DESC) {
                    sorts.add(Sort.by(Sort.Direction.DESC, alias));
                } else {
                    String paramsTemplate = filterType(filterType);
                    if (isNotBlank) {
                        builder.append(join);
                    }
                    builder.append(Strings.lenientFormat(FIELD_TEMPLATE, alias))
                            .append(Strings.lenientFormat(paramsTemplate, paramsName));
                    String finalParamsName = paramsName;
                    isNotBlank = true;
                    queryConsumer = queryConsumer.andThen(query -> query.setParameter(finalParamsName, value));
                }
            }
        }
        return queryConsumer;
    }


    private static List<QueryField<?>> buildQueryField(QueryConfig queryConfig, Object value) {
        if (value instanceof QueryField) {
            return Collections.singletonList((QueryField<?>) value);
        }
        if (queryConfig != null) {
            return Collections.singletonList(new QueryField<>(queryConfig.filterType(), value));
        }
        if (value instanceof Collection) {
            if (((Collection) value).isEmpty()) {
                return Collections.emptyList();
            }
            Object item = ((Collection<?>) value).iterator().next();
            if (item instanceof QueryField) {
                return Lists.newArrayList((Collection<QueryField<?>>) value);
            } else {
                return Collections.singletonList(new QueryField<>(QueryFilterType.CONTAINS, value));
            }
        }
        return Collections.singletonList(new QueryField<>(QueryFilterType.EQ, value));
    }


    private static String filterType(QueryFilterType filterType) {
        switch (filterType) {
            case EQ:
                return " = " + PARAMS_TEMPLATE;
            case CONTAINS:
                return " in ( " + PARAMS_TEMPLATE + " )";
            case NOT_CONTAINS:
                return " not in ( " + PARAMS_TEMPLATE + " )";
            case GE:
                return " >= " + PARAMS_TEMPLATE;
            case GT:
                return " > " + PARAMS_TEMPLATE;
            case LT:
                return " < " + PARAMS_TEMPLATE;
            case LE:
                return " <= " + PARAMS_TEMPLATE;
            case NOT_EQ:
                return " <> " + PARAMS_TEMPLATE;
            case LIKE:
                return " like CONCAT('%'," + PARAMS_TEMPLATE + ",'%')";
            case STARTS_WITH:
                return " like CONCAT(" + PARAMS_TEMPLATE + ",'%')";
            case ENDS_WITH:
                return " like CONCAT('%'," + PARAMS_TEMPLATE + ")";
            default:
                throw new IllegalArgumentException(Strings.lenientFormat("不支持的查询表达式[%s]~", filterType.name()));
        }

    }


}
