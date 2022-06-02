package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.cloud.common.StringConstants;
import com.google.common.base.Strings;
import org.jetbrains.annotations.Nullable;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author davies
 * @date 2022/5/4 19:57
 */
class DefaultJpaQueryHandle implements JpaQueryHandle {


    @Override
    public boolean support(QueryFilterType queryFilterType,
                           Object value, String column, @Nullable Field field) {
        return true;
    }

    @Override
    public <T> Predicate handle(QueryFilterType queryFilterType,
                                Object value,
                                List<Order> orders,
                                String column,
                                Path<T> root,
                                CriteriaBuilder cb,
                                @Nullable Field field) {

        switch (queryFilterType) {
            case LIKE:
                return likePredicate(column, root, cb, StringConstants.PERCENT + value.toString() + StringConstants.PERCENT);
            case STARTS_WITH:
                return likePredicate(column, root, cb, value.toString() + StringConstants.PERCENT);
            case ENDS_WITH:
                return likePredicate(column, root, cb, StringConstants.PERCENT + value.toString());
            case EQ:
                return cb.equal(root.get(column), value);
            case NOT_EQ:
                return cb.notEqual(root.get(column), value);
            case CONTAINS:
                return containsPredicate(column, root, value, null);
            case NOT_CONTAINS:
                return containsPredicate(column, root, value, Predicate::not);
            case LT:
                return comparablePredicate(root.get(column), value, cb::lessThan);
            case LE:
                return comparablePredicate(root.get(column), value, cb::lessThanOrEqualTo);
            case GT:
                return comparablePredicate(root.get(column), value, cb::greaterThan);
            case GE:
                return comparablePredicate(root.get(column), value, cb::greaterThanOrEqualTo);
            case ASC:
                orders.add(cb.asc(root.get(column)));
                return null;
            case DESC:
                orders.add(cb.desc(root.get(column)));
                return null;
            default:
                throw new IllegalArgumentException(Strings.lenientFormat("FilterType [%s] 不支持", queryFilterType.name()));
        }
    }

    private <T> Predicate containsPredicate(String column, Path<T> root, Object value, Function<Predicate, Predicate> predicateFunction) {
        Predicate predicate;
        if (value instanceof Collection) {
            predicate = root.get(column).in(((Collection<?>) value).toArray());
        } else {
            predicate = root.get(column).in(value);
        }
        if (predicateFunction != null) {
            return predicateFunction.apply(predicate);
        }
        return predicate;

    }

    private <T> Predicate likePredicate(String column,
                                        Path<T> root,
                                        CriteriaBuilder cb,

                                        String pattern) {
        return cb.like(root.get(column), pattern);
    }


    @SuppressWarnings("unchecked")
    private Predicate comparablePredicate(
            Expression<? extends Comparable<Object>> expression,
            Object value, BiFunction<Expression<? extends Comparable<Object>>, Comparable<Object>, Predicate> biFunction) {
        if (value instanceof Comparable) {
            Comparable<Object> comparable = (Comparable<Object>) value;
            return biFunction.apply(expression, comparable);
        }
        throw new IllegalArgumentException("Filter must implements Comparable");
    }

}
