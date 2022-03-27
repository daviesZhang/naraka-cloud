package com.davies.naraka.puppeteer;

import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.cloud.common.StringConstants;
import com.davies.naraka.puppeteer.annotation.QueryParams;
import com.google.common.base.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Locale.ENGLISH;

/**
 * @author davies
 * @date 2022/3/26 21:14
 */
public class SimpleQuery {
    private static final Pattern FROM_PATTERN = Pattern.compile("select.*?from", Pattern.CASE_INSENSITIVE);
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$", Pattern.CASE_INSENSITIVE);
    private static final String COUNT_FROM = "SELECT COUNT(*) FROM";
    private static final String FROM_TEMPLATE = "SELECT c FROM %s c";

    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String SORT = "sort";
    private static final String DESC = "desc";

    private final EntityManager entityManager;

    public SimpleQuery(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Page getPage(String from, Object queryParams) {
        return getPage(from, queryParams, null, null);
    }


    public <T> Page<T> getPage(String from, Object queryParams, Class<T> tClass) {
        return getPage(from, queryParams, tClass, null);
    }

    public <T> Page<T> getPage(Object queryParams, Class<T> tClass) {
        return getPage(buildFrom(tClass), queryParams, tClass, null);
    }

    public <T, V> Page<V> getPage(Object queryParams, Class<T> tClass, Function<T, V> convert) {
        return getPage(buildFrom(tClass), queryParams, tClass, convert);
    }

    public <T, V> Page<V> getPage(String from, Object queryParams, Class<T> tClass, Function<T, V> convert) {
        QueryHelper queryHelper = query(queryParams, true);
        String countFrom = FROM_PATTERN
                .matcher(from)
                .replaceAll(COUNT_FROM);
        TypedQuery<Long> countQuery = entityManager.createQuery(countFrom + queryHelper.getQuerySql(), Long.class);
        Function<Query, Query> function = queryHelper.getParamsFunction();
        if (function != null) {
            function.apply(countQuery);
        }
        Long count = countQuery.getSingleResult();
        Pageable pageable = queryHelper.getPageable();
        if (count <= 0) {
            return Page.empty(pageable);
        }

        List<T> list = list(from, queryHelper, tClass);
        if (convert != null) {
            List<V> result = list.stream().map(convert).collect(Collectors.toList());
            return new PageImpl<>(result, pageable, count);
        }
        return new PageImpl<>((List<V>) list, pageable, count);
    }


    public <T, V> List<V> getList(String from, Object queryParams, Class<T> tClass, Function<T, V> convert) {
        return getList(from, queryParams, tClass)
                .stream().map(convert).collect(Collectors.toList());
    }

    public <T, V> List<V> getList(Object queryParams, Class<T> tClass, Function<T, V> convert) {
        return getList(buildFrom(tClass), queryParams, tClass)
                .stream().map(convert).collect(Collectors.toList());
    }


    public List getList(String from, Object queryParams) {
        return getList(from, queryParams, null);
    }


    public <T> List<T> getList(String from, Object queryParams, Class<T> tClass) {
        QueryHelper queryHelper = query(queryParams, false);
        return list(from, queryHelper, tClass);
    }

    public <T> List<T> getList(Object queryParams, Class<T> tClass) {
        QueryHelper queryHelper = query(queryParams, false);
        return list(buildFrom(tClass), queryHelper, tClass);
    }


    private <T> List<T> list(String from, QueryHelper queryHelper, Class<T> tClass) {
        Query query;
        if (null == tClass) {
            query = entityManager.createQuery(from + queryHelper.getQuerySql());
        } else {
            query = entityManager.createQuery(from + queryHelper.getQuerySql(), tClass);
        }
        Function<Query, Query> function = queryHelper.getParamsFunction();
        if (function != null) {
            function.apply(query);
        }
        return query.getResultList();
    }


    private String buildFrom(Class from) {
        return Strings.lenientFormat(FROM_TEMPLATE, from.getName());
    }


    private static QueryHelper query(Object o, boolean isPage) {

        Class<?> oClass = o.getClass();
        Field[] fields = oClass.getDeclaredFields();
        StringBuilder builder = new StringBuilder();
        String AND = " AND ";
        String FIELD = "%s ";
        boolean isNotBlank = false;
        ParameterLink queryFunction = null;
        Pageable pageable = null;
        Integer page = null;
        Integer size = null;
        List<String> sort = null;
        for (Field field : fields) {
            QueryParams queryParams = field.getDeclaredAnnotation(QueryParams.class);
            if (null != queryParams && queryParams.skip()) {
                continue;
            }
            Object params;
            try {
                MethodHandle handle = MethodHandles.lookup().findVirtual(oClass, "get" + capitalize(field.getName()), MethodType.methodType(String.class));
                params = handle.invoke(o);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            String alias = field.getName();
            if (queryParams != null && !Strings.isNullOrEmpty(queryParams.alias())) {
                alias = queryParams.alias();
            }

            if (isPage) {

                if (PAGE.equals(alias)) {

                }
            }
            if (params == null) {
                continue;
            }
            if (isNotBlank) {
                builder.append(AND);
            }
            if (!isNotBlank) {
                isNotBlank = true;
            }

            String paramsTemplate = filterType(queryParams == null ? QueryFilterType.EQUALS : queryParams.filterType());
            builder.append(Strings.lenientFormat(FIELD, alias))
                    .append(Strings.lenientFormat(paramsTemplate, field.getName()));
            queryFunction = new ParameterLink(field.getName(), params, queryFunction);
        }
        String querySql;
        if (isNotBlank) {
            querySql = builder.insert(0, " where ").toString();
        } else {
            querySql = StringConstants.EMPTY;
        }
        return new QueryHelper(querySql, queryFunction);
    }

    private int objectToInt(Object value, int defaultValue) {
        if (null == value) {
            return defaultValue;
        }
        if (value instanceof Integer) {
            return (int) value;
        }
        if (value instanceof Long) {
            return (int) value;
        }
        String valueString = value.toString();
        if (NUMBER_PATTERN.matcher(valueString).matches()) {
            return Integer.parseInt(valueString);
        }
        return defaultValue;
    }


    private static String filterType(QueryFilterType filterType) {
        String PARAMS = " :%s";
        switch (filterType) {
            case CONTAINS:
                return " in ( " + PARAMS + " )";
            case NOT_CONTAINS:
                return " not in ( " + PARAMS + " )";
            case GREATERTHANEQUAL:
                return " >= " + PARAMS;
            case EQUALS:
            default:
                return " = " + PARAMS;
        }

    }


    private static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    private static class ParameterLink implements Function<Query, Query> {
        protected final ParameterLink previous;
        private final String name;
        private final Object params;

        protected ParameterLink(String name, Object params, ParameterLink previous) {
            this.previous = previous;
            this.name = name;
            this.params = params;
        }

        @Override
        public Query apply(Query query) {
            return (previous != null ? previous.apply(query) : query).setParameter(name, params);
        }
    }

    private static class QueryHelper {

        private String querySql;

        private Pageable pageable;

        private Function<Query, Query> paramsFunction;

        public Pageable getPageable() {
            return pageable;
        }

        public void setPageable(Pageable pageable) {
            this.pageable = pageable;
        }

        public QueryHelper(String querySql, Function<Query, Query> paramsFunction) {
            this.querySql = querySql;
            this.paramsFunction = paramsFunction;
        }

        public String getQuerySql() {
            return querySql;
        }

        public void setQuerySql(String querySql) {
            this.querySql = querySql;
        }

        public Function<Query, Query> getParamsFunction() {
            return paramsFunction;
        }

        public void setParamsFunction(Function<Query, Query> paramsFunction) {
            this.paramsFunction = paramsFunction;
        }
    }


}
