package com.davies.naraka.puppeteer;

import com.davies.naraka.autoconfigure.annotation.ColumnName;
import com.davies.naraka.autoconfigure.annotation.QuerySkip;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.cloud.common.StringConstants;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Locale.ENGLISH;

/**
 * @author sycho
 */
@Slf4j
public class JpaSpecificationUtils {
    private static final String READ_METHOD = "get";

    public static <T> Specification<T> specification(Object queryParams) {
        return (root, query, criteriaBuilder) -> {
            Class<?> classes = queryParams.getClass();
            Field[] fields = classes.getDeclaredFields();
            Predicate predicate = null;
            List<Order> orders = Lists.newArrayList();
            Map<String, Join<T, ?>> cacheJoin = new HashMap<>(3);
            for (Field declaredField : fields) {
                if (declaredField.isAnnotationPresent(QuerySkip.class)) {
                    continue;
                }
                String name = declaredField.getName();
                Object value;
                try {
                    Method method = BeanUtils.findDeclaredMethodWithMinimalParameters(classes, READ_METHOD + capitalize(name));
                    if (null == method) {
                        continue;
                    }
                    value = method.invoke(queryParams);
                } catch (IllegalArgumentException | IllegalAccessException |
                        InvocationTargetException e) {
                    log.debug("BeanUtils.findDeclaredMethodWithMinimalParameters", e);
                    continue;
                }
                if (value == null) {
                    continue;
                }
                String column = name;
                ColumnName columnName = declaredField.getDeclaredAnnotation(ColumnName.class);
                if (columnName != null && !Strings.isNullOrEmpty(columnName.name())) {
                    column = columnName.name();
                }
                QueryField<?> queryField = getQueryField(value);
                JoinQuery joinQuery = declaredField.getDeclaredAnnotation(JoinQuery.class);
                Expression<String> expression;
                if (joinQuery != null) {
                    Join<T, ?> join;
                    if (cacheJoin.containsKey(joinQuery.attributeName())) {
                        join = cacheJoin.get(joinQuery.attributeName());
                    } else {
                        join = root.join(joinQuery.attributeName(), joinQuery.joinType());
                        cacheJoin.put(joinQuery.attributeName(), join);
                    }
                    expression = join.get(column);
                } else {
                    expression = root.get(column);
                }
                Predicate columnPredicate = getPredicate(
                        queryField,
                        expression,
                        orders,
                        query,
                        criteriaBuilder);
                if (columnPredicate == null) {
                    continue;
                }
                if (predicate != null) {
                    predicate = criteriaBuilder.and(predicate, columnPredicate);
                } else {
                    predicate = columnPredicate;
                }
            }
            return query.orderBy(orders).where(predicate).getRestriction();
        };
    }

    private static QueryField<?> getQueryField(Object value) {
        QueryField<?> queryField;
        if (value instanceof QueryField) {
            queryField = (QueryField<?>) value;
        } else {
            if (value instanceof Collection) {
                queryField = new QueryField<>(QueryFilterType.CONTAINS, (Collection<?>) value);
            } else {
                queryField = new QueryField<>(QueryFilterType.EQUALS, value);
            }
        }
        return queryField;
    }

    private static Predicate getPredicate(
            QueryField<?> queryField,
            Expression<?> column,
            List<Order> orders,
            CriteriaQuery<?> query,
            CriteriaBuilder cb) {
        Object value = queryField.getFilter();
        QueryFilterType queryFilterType = queryField.getType();

        switch (queryFilterType) {
            case LIKE:
                return cb.like((Expression<String>) column, StringConstants.PERCENT + value.toString() + StringConstants.PERCENT);
            case EQUALS:
                return cb.equal(column, value);
            case CONTAINS:
                if (value instanceof Collection) {
                    return column.in(((Collection<?>) value).toArray());
                }
                return column.in(value);
            case NOT_CONTAINS:
                if (value instanceof Collection) {
                    return column.in(((Collection<?>) value).toArray()).not();
                }
                return column.in(value).not();
            case LESSTHAN:

                return cb.lessThan(column, value).in();
            case ORDER_ASC:
                orders.add(cb.asc(column));
                return null;
            case ORDER_DESC:
                orders.add(cb.desc(column));
                return null;
            default:
                throw new IllegalArgumentException(Strings.lenientFormat("FilterType [%s] 不支持", queryFilterType.name()));
        }
    }

    private static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }
}
