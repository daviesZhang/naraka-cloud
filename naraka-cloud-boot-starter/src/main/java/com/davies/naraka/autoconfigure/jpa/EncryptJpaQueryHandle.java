package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.autoconfigure.SupportEncryptHelper;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.autoconfigure.jpa.function.AesDecryptFunction;
import com.davies.naraka.autoconfigure.jpa.function.UnHexFunction;
import com.davies.naraka.autoconfigure.properties.EncryptProperties;
import com.davies.naraka.cloud.common.StringConstants;
import com.google.common.base.Strings;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.jetbrains.annotations.Nullable;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.davies.naraka.autoconfigure.SupportEncryptHelper.encrypt;

/**
 * @author davies
 * @date 2022/5/4 19:57
 */
class EncryptJpaQueryHandle implements JpaQueryHandle {

    private final EncryptProperties encryptProperties;

    public EncryptJpaQueryHandle(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }

    @Override
    public boolean support(QueryFilterType filterType,
                           Object value, String column, @Nullable Field field) {
        return SupportEncryptHelper.support(filterType, column, encryptProperties, field);
    }

    @Override
    public <T> Predicate handle(QueryFilterType queryFilterType,
                                Object value,
                                List<Order> orders,
                                String column,
                                Path<T> root,
                                CriteriaBuilder cb,
                                @Nullable Field field) {

        String key = SupportEncryptHelper.getEncryptKey(column, encryptProperties, field);
        switch (queryFilterType) {
            case LIKE:
                return likePredicate(column, root, cb, key, StringConstants.PERCENT + value.toString() + StringConstants.PERCENT);
            case STARTS_WITH:
                return likePredicate(column, root, cb, key, value.toString() + StringConstants.PERCENT);
            case ENDS_WITH:
                return likePredicate(column, root, cb, key, StringConstants.PERCENT + value.toString());
            case EQ:
                return cb.equal(root.get(column), encrypt(value, key));
            case NOT_EQ:
                return cb.notEqual(root.get(column), encrypt(value, key));
            case CONTAINS:
                return containsPredicate(column, root, key, value, null);
            case NOT_CONTAINS:
                return containsPredicate(column, root, key, value, Predicate::not);
            default:
                throw new IllegalArgumentException(Strings.lenientFormat("FilterType [%s] 不支持", queryFilterType.name()));
        }
    }


    private <T> Predicate containsPredicate(String column, Path<T> root, String key, Object value, Function<Predicate, Predicate> predicateFunction) {
        Predicate predicate;
        if (value instanceof Collection) {

            Object[] values = ((Collection<?>) value).stream().map(v -> encrypt(v, key)).toArray();
            predicate = root.get(column).in(values);

        } else {
            predicate = root.get(column).in(encrypt(value, key));
        }
        if (predicateFunction != null) {
            return predicateFunction.apply(predicate);
        }
        return predicate;

    }

    private <T> Predicate likePredicate(String column,
                                        Path<T> root,
                                        CriteriaBuilder cb,
                                        String key,
                                        String pattern) {

        CriteriaBuilderImpl criteriaBuilder = (CriteriaBuilderImpl) cb;
        Expression<String> expression = new AesDecryptFunction(criteriaBuilder, new UnHexFunction(criteriaBuilder, root.get(column)), key);
        return cb.like(expression, pattern);
    }


}
