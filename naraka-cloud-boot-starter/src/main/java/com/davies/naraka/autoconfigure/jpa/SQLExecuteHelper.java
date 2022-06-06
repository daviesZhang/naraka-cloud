package com.davies.naraka.autoconfigure.jpa;

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author davies
 * @date 2022/3/26 21:14
 */
public class SQLExecuteHelper {
    private static final Pattern FROM_PATTERN = Pattern.compile("SELECT.*?FROM", Pattern.CASE_INSENSITIVE);

    private static final String COUNT_FROM = "SELECT COUNT(*) FROM";

    private static final String SELECT_FROM_TEMPLATE = "SELECT c FROM %s c";

    private static final String UPDATE_FROM_TEMPLATE = "UPDATE %s c ";


    private final EntityManager entityManager;


    public SQLExecuteHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Page getPage(@NotNull String from, @NotNull QuerySQLParams QuerySQLParams) {
        return getPage(from, QuerySQLParams, null, null);
    }


    public <T> Page<T> getPage(@NotNull String from, @NotNull QuerySQLParams QuerySQLParams, @NotNull Class<T> tClass) {
        return getPage(from, QuerySQLParams, tClass, null);
    }

    public <T> Page<T> getPage(@NotNull QuerySQLParams QuerySQLParams, @NotNull Class<T> tClass) {
        return getPage(buildSelectFrom(tClass), QuerySQLParams, tClass, null);
    }

    public <T, V> Page<V> getPage(@NotNull QuerySQLParams QuerySQLParams, @NotNull Class<T> tClass, @NotNull Function<T, V> convert) {
        return getPage(buildSelectFrom(tClass), QuerySQLParams, tClass, convert);
    }

    public <T, V> @NotNull Page<V> getPage(@NotNull String from, @NotNull QuerySQLParams QuerySQLParams, Class<T> tClass, Function<T, V> convert) {
        Pageable pageable = QuerySQLParams.getPageable();
        Assert.notNull(pageable, "QueryParams#Pageable must not be null");
        String countFrom = FROM_PATTERN
                .matcher(from)
                .replaceAll(COUNT_FROM);
        TypedQuery<Long> countQuery = entityManager.createQuery(countFrom + QuerySQLParams.getSql(), Long.class);
        QuerySQLParams.getQueryConsumer().apply(countQuery);

        Long count = countQuery.getSingleResult();
        if (count <= 0) {
            return Page.empty(pageable);
        }
        Function<Query, Query> queryConsumer = QuerySQLParams.getQueryConsumer();
        queryConsumer = queryConsumer.andThen(query -> {
            query.setFirstResult(pageable.getPageSize() * pageable.getPageNumber());
            query.setMaxResults(pageable.getPageSize());
            return query;
        });
        QuerySQLParams listParams = new QuerySQLParams(QuerySQLParams.getSql(), queryConsumer, pageable);
        List<T> list = list(from, listParams, tClass);
        if (convert != null) {
            List<V> result = list.stream().map(convert).collect(Collectors.toList());
            return new PageImpl<>(result, pageable, count);
        }
        return new PageImpl<>((List<V>) list, pageable, count);
    }


    public <T, V> List<V> getList(@NotNull String from, @NotNull QuerySQLParams QuerySQLParams, @NotNull Class<T> tClass, @NotNull Function<T, V> convert) {
        return getList(from, QuerySQLParams, tClass)
                .stream().map(convert).collect(Collectors.toList());
    }


    public <T, V> List<V> getList(@NotNull QuerySQLParams QuerySQLParams, @NotNull Class<T> tClass, @NotNull Function<T, V> convert) {
        return getList(buildSelectFrom(tClass), QuerySQLParams, tClass)
                .stream().map(convert).collect(Collectors.toList());
    }


    public List getList(@NotNull String from, @NotNull QuerySQLParams QuerySQLParams) {
        return getList(from, QuerySQLParams, null);
    }


    public <T> List<T> getList(@NotNull String from, @NotNull QuerySQLParams QuerySQLParams, @NotNull Class<T> tClass) {
        return list(from, QuerySQLParams, tClass);
    }

    public <T> List<T> getList(@NotNull QuerySQLParams QuerySQLParams, @NotNull Class<T> tClass) {
        return list(buildSelectFrom(tClass), QuerySQLParams, tClass);
    }


    private <T> List<T> list(@NotNull String from, @NotNull QuerySQLParams QuerySQLParams, Class<T> tClass) {
        Query query;
        if (null == tClass) {
            query = entityManager.createQuery(from + QuerySQLParams.getSql());
        } else {
            query = entityManager.createQuery(from + QuerySQLParams.getSql(), tClass);
        }
        Function<Query, Query> function = QuerySQLParams.getQueryConsumer();
        if (function != null) {
            function.apply(query);
        }
        return query.getResultList();
    }


    public <T> int update(@NotNull QuerySQLParams querySQLParams, @NotNull SQLParams updateParams, @NotNull Class<T> tClass) {
        String hql = buildUpdateFrom(tClass) + updateParams.getSql() + querySQLParams.getSql();
        Query query = entityManager.createQuery(hql);
        Function<Query, Query> queryConsumer = querySQLParams.getQueryConsumer();
        if (queryConsumer != null) {
            queryConsumer.apply(query);
        }
        Function<Query, Query> updateParamsQueryConsumer = updateParams.getQueryConsumer();
        if (updateParamsQueryConsumer != null) {
            updateParamsQueryConsumer.apply(query);
        }
        return query.executeUpdate();

    }


    private @NotNull String getTableName(@NotNull Class<?> from) {
        Entity entity = from.getAnnotation(Entity.class);
        String tableName = from.getName();
        if (null != entity) {
            String name = entity.name();
            if (!Strings.isNullOrEmpty(name)) {
                tableName = name;
            }
        }
        return tableName;
    }

    private @NotNull String buildUpdateFrom(@NotNull Class<?> from) {

        return Strings.lenientFormat(UPDATE_FROM_TEMPLATE, getTableName(from));
    }

    private @NotNull String buildSelectFrom(@NotNull Class<?> from) {

        return Strings.lenientFormat(SELECT_FROM_TEMPLATE, getTableName(from));
    }


}
