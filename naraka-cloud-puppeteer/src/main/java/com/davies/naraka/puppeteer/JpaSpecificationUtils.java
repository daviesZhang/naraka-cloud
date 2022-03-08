package com.davies.naraka.puppeteer;

import com.davies.naraka.autoconfigure.annotation.ColumnName;
import com.davies.naraka.autoconfigure.annotation.Crypto;
import com.davies.naraka.autoconfigure.annotation.QuerySkip;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.autoconfigure.properties.EncryptProperties;
import com.davies.naraka.cloud.common.AesEncryptorUtils;
import com.davies.naraka.cloud.common.StringConstants;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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

    private final EncryptProperties encryptProperties;

    public JpaSpecificationUtils(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }

    public <T> Specification<T> specification(Object queryParams) {
        return (root, query, criteriaBuilder) -> {
            Class<?> classes = queryParams.getClass();
            Field[] fields = classes.getDeclaredFields();
            Predicate predicate = null;
            List<Order> orders = Lists.newArrayList();
            Map<String, Path<T>> cacheJoin = new HashMap<>(3);
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
                Predicate nextPredicate = null;
                if (value instanceof Collection) {
                    for (Object v : (Collection) value) {
                        if (nextPredicate != null) {
                            nextPredicate = criteriaBuilder.and(nextPredicate, getPredicate(root, criteriaBuilder, orders, cacheJoin, declaredField, v, column));
                        } else {
                            nextPredicate = getPredicate(root, criteriaBuilder, orders, cacheJoin, declaredField, v, column);
                        }
                    }
                } else {
                    nextPredicate = getPredicate(root, criteriaBuilder, orders, cacheJoin, declaredField, value, column);
                }
                if (null == nextPredicate) {
                    continue;
                }
                if (predicate != null) {
                    predicate = criteriaBuilder.and(predicate, nextPredicate);
                } else {
                    predicate = nextPredicate;
                }
            }
            return query.orderBy(orders).where(predicate).getRestriction();
        };
    }

    private <T> Predicate getPredicate(Root<T> root, CriteriaBuilder criteriaBuilder, List<Order> orders, Map<String, Path<T>> cacheJoin, Field declaredField, Object value, String column) {

        QueryField<?> queryField = getQueryField(value);
        JoinQuery joinQuery = declaredField.getDeclaredAnnotation(JoinQuery.class);
        Path<T> path;
        if (joinQuery != null) {
            if (cacheJoin.containsKey(joinQuery.attributeName())) {
                path = cacheJoin.get(joinQuery.attributeName());
            } else {
                path = root.join(joinQuery.attributeName(), joinQuery.joinType());
                cacheJoin.put(joinQuery.attributeName(), path);
            }
        } else {
            path = root;
        }
        return getPredicateByType(
                queryField,
                orders,
                column,
                path,
                criteriaBuilder, getEncryptKey(declaredField));
    }


    private QueryField<?> getQueryField(Object value) {
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

    private <T> Predicate getPredicateByType(
            QueryField<?> queryField,
            List<Order> orders,
            String column,
            Path<T> root,
            CriteriaBuilder cb,
            String key) {
        Object value = queryField.getFilter();
        QueryFilterType queryFilterType = queryField.getType();

        switch (queryFilterType) {
            case LIKE:
                return cb.like(root.get(column), StringConstants.PERCENT + value.toString() + StringConstants.PERCENT);
            case STARTS_WITH:
                return cb.like(root.get(column), value.toString() + StringConstants.PERCENT);
            case ENDS_WITH:
                return cb.like(root.get(column), StringConstants.PERCENT + value.toString());
            case EQUALS:
                return cb.equal(root.get(column), Strings.isNullOrEmpty(key) ? value : encrypt(value, key));
            case NOT_EQUALS:
                return cb.notEqual(root.get(column), Strings.isNullOrEmpty(key) ? value : encrypt(value, key));
            case CONTAINS:
                if (value instanceof Collection) {
                    if (Strings.isNullOrEmpty(key)) {
                        return root.get(column).in(((Collection<?>) value).toArray());
                    } else {
                        Object[] values = ((Collection<?>) value).stream().map(v -> encrypt(v, key)).toArray();
                        return root.get(column).in(values);
                    }
                }
                return root.get(column).in(Strings.isNullOrEmpty(key) ? value : encrypt(value, key));
            case NOT_CONTAINS:
                if (value instanceof Collection) {
                    if (Strings.isNullOrEmpty(key)) {
                        return root.get(column).in(((Collection<?>) value).toArray()).not();
                    } else {
                        Object[] values = ((Collection<?>) value).stream().map(v -> encrypt(v, key)).toArray();
                        return root.get(column).in(values).not();
                    }
                }
                return root.get(column).in(Strings.isNullOrEmpty(key) ? value : encrypt(value, key)).not();
            case LESSTHAN:
            case LESSTHANEQUAL:
            case GREATERTHANE:
            case GREATERTHANEQUAL:
                return comparablePredicate(queryFilterType, cb, root.get(column), value);
            case ORDER_ASC:
                orders.add(cb.asc(root.get(column)));
                return null;
            case ORDER_DESC:
                orders.add(cb.desc(root.get(column)));
                return null;
            default:
                throw new IllegalArgumentException(Strings.lenientFormat("FilterType [%s] 不支持", queryFilterType.name()));
        }

    }


    private Predicate comparablePredicate(QueryFilterType queryFilterType,
                                          CriteriaBuilder cb,
                                          Expression<? extends Comparable> expression,
                                          Object value) {
        if (value instanceof Comparable) {
            Comparable<Object> comparable = (Comparable<Object>) value;
            switch (queryFilterType) {
                case LESSTHAN:
                    return cb.lessThan(expression, comparable);
                case LESSTHANEQUAL:
                    return cb.lessThanOrEqualTo(expression, comparable);
                case GREATERTHANE:
                    return cb.greaterThan(expression, comparable);
                case GREATERTHANEQUAL:
                    return cb.greaterThanOrEqualTo(expression, comparable);
                default:
                    throw new IllegalArgumentException(Strings.lenientFormat("FilterType [%s] 不支持", queryFilterType.name()));
            }
        }
        throw new IllegalArgumentException("Filter must implements Comparable");
    }


    private String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
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

    private String getEncryptKey(Field field) {
        if (encryptProperties == null || !encryptProperties.isEnable()) {
            return null;
        }
        Crypto crypto = field.getDeclaredAnnotation(Crypto.class);
        String key = null;
        if (crypto != null) {
            key = encryptProperties.getKey(Strings.isNullOrEmpty(crypto.name()) ? field.getName() : crypto.name());
        }
        return key;
    }

    private String encrypt(Object object, String key) {
        try {
            if (object instanceof String) {
                return AesEncryptorUtils.encrypt((String) object, key);
            }
            throw new IllegalArgumentException(key + ":加密异常,值必须为String类型");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {

            throw new RuntimeException(key + ":加密异常", e);
        }

    }

}
