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
public class SimpleQueryHelper {
    private static final Pattern FROM_PATTERN = Pattern.compile("select.*?from", Pattern.CASE_INSENSITIVE);

    private static final String COUNT_FROM = "SELECT COUNT(*) FROM";

    private static final String FROM_TEMPLATE = "SELECT c FROM %s c";


    private final EntityManager entityManager;


    public SimpleQueryHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Page getPage(@NotNull String from, @NotNull QueryParams queryParams) {
        return getPage(from, queryParams, null, null);
    }


    public <T> Page<T> getPage(@NotNull String from, @NotNull QueryParams queryParams, @NotNull Class<T> tClass) {
        return getPage(from, queryParams, tClass, null);
    }

    public <T> Page<T> getPage(@NotNull QueryParams queryParams, @NotNull Class<T> tClass) {
        return getPage(buildFrom(tClass), queryParams, tClass, null);
    }

    public <T, V> Page<V> getPage(@NotNull QueryParams queryParams, @NotNull Class<T> tClass, @NotNull Function<T, V> convert) {
        return getPage(buildFrom(tClass), queryParams, tClass, convert);
    }

    public <T, V> @NotNull Page<V> getPage(@NotNull String from, @NotNull QueryParams queryParams, Class<T> tClass, Function<T, V> convert) {
        Pageable pageable = queryParams.getPageable();
        Assert.notNull(pageable, "QueryParams#Pageable must not be null");
        String countFrom = FROM_PATTERN
                .matcher(from)
                .replaceAll(COUNT_FROM);
        TypedQuery<Long> countQuery = entityManager.createQuery(countFrom + queryParams.getQuerySql(), Long.class);
        queryParams.getQueryConsumer().apply(countQuery);

        Long count = countQuery.getSingleResult();
        if (count <= 0) {
            return Page.empty(pageable);
        }
        Function<Query, Query> queryConsumer = queryParams.getQueryConsumer();
        queryConsumer = queryConsumer.andThen(query -> {
            query.setFirstResult(pageable.getPageSize() * pageable.getPageNumber());
            query.setMaxResults(pageable.getPageSize());
            return query;
        });
        QueryParams listParams = new QueryParams(queryParams.getQuerySql(), queryConsumer, pageable);
        List<T> list = list(from, listParams, tClass);
        if (convert != null) {
            List<V> result = list.stream().map(convert).collect(Collectors.toList());
            return new PageImpl<>(result, pageable, count);
        }
        return new PageImpl<>((List<V>) list, pageable, count);
    }


    public <T, V> List<V> getList(@NotNull String from, @NotNull QueryParams queryParams, @NotNull Class<T> tClass, @NotNull Function<T, V> convert) {
        return getList(from, queryParams, tClass)
                .stream().map(convert).collect(Collectors.toList());
    }


    public <T, V> List<V> getList(@NotNull QueryParams queryParams, @NotNull Class<T> tClass, @NotNull Function<T, V> convert) {
        return getList(buildFrom(tClass), queryParams, tClass)
                .stream().map(convert).collect(Collectors.toList());
    }


    public List getList(@NotNull String from, @NotNull QueryParams queryParams) {
        return getList(from, queryParams, null);
    }


    public <T> List<T> getList(@NotNull String from, @NotNull QueryParams queryParams, @NotNull Class<T> tClass) {
        return list(from, queryParams, tClass);
    }

    public <T> List<T> getList(@NotNull QueryParams queryParams, @NotNull Class<T> tClass) {
        return list(buildFrom(tClass), queryParams, tClass);
    }


    private <T> List<T> list(@NotNull String from, @NotNull QueryParams queryParams, Class<T> tClass) {
        Query query;
        if (null == tClass) {
            query = entityManager.createQuery(from + queryParams.getQuerySql());
        } else {
            query = entityManager.createQuery(from + queryParams.getQuerySql(), tClass);
        }
        Function<Query, Query> function = queryParams.getQueryConsumer();
        if (function != null) {
            function.apply(query);
        }
        return query.getResultList();
    }


    private @NotNull String buildFrom(@NotNull Class<?> from) {
        Entity entity = from.getAnnotation(Entity.class);
        String tableName = from.getName();
        if (null != entity) {
            String name = entity.name();
            if (!Strings.isNullOrEmpty(name)) {
                tableName = name;
            }
        }
        return Strings.lenientFormat(FROM_TEMPLATE, tableName);
    }


}
