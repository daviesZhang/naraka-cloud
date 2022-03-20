package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.QueryPage;
import com.davies.naraka.autoconfigure.annotation.ColumnName;
import com.davies.naraka.autoconfigure.annotation.Crypto;
import com.davies.naraka.autoconfigure.annotation.QueryFilter;
import com.davies.naraka.autoconfigure.annotation.QuerySkip;
import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.autoconfigure.jpa.function.AesDecryptFunction;
import com.davies.naraka.autoconfigure.jpa.function.UnHexFunction;
import com.davies.naraka.autoconfigure.properties.EncryptProperties;
import com.davies.naraka.cloud.common.AesEncryptorUtils;
import com.davies.naraka.cloud.common.StringConstants;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Locale.ENGLISH;

/**
 * Specification查询构造工具类
 * 支持多表链接,支持查询加密数据
 *
 * @author sycho
 * @see JoinQuery
 * @see QueryField
 */
@Slf4j
public class JpaSpecificationUtils {
    private static final String READ_METHOD = "get";

    private final EncryptProperties encryptProperties;

    public JpaSpecificationUtils(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }


    /**
     * 分页获取数据
     * @param query 查询参数
     * @param findPage 分页查询的实际方法
     * @param supplier 需要转换对象的实例获取方法
     * @param <T> 返回的DTO
     * @param <E> entity
     * @param <Q> 查询对象
     * @return PageDTO
     */
    public <T, E, Q> PageDTO<T> pageQuery(
                                          QueryPage<Q> query,
                                          BiFunction<Specification<E>, Pageable, Page<E>> findPage,
                                          Supplier<T> supplier) {
        Pageable pageable=   Pageable.ofSize(Math.toIntExact(query.getSize()))
                .withPage(Math.toIntExact(query.getCurrent()));
        Specification<E> specification = specification(query.getQuery());
        Page<T> page= findPage.apply(specification, pageable).map(e -> ClassUtils.copyObject(e, supplier.get()));
        List<T> items = page.getContent();
        return new PageDTO<>(query.getCurrent(), (long) page.getTotalPages(), query.getSize(), items);
    }

    public <T, E, Q> PageDTO<T> pageQuery(
            QueryPage<Q> query,
            JpaSpecificationExecutor<E> executor,
            Supplier<T> supplier) {
        return this.pageQuery( query, (BiFunction<Specification<E>, Pageable, Page<E>>) executor::findAll,supplier);
    }


    /**
     * 分页获取数据
     * @param query 查询参数
     * @param findPage 分页查询的实际方法
     * @param <E> entity
     * @param <Q> 查询对象
     * @return PageDTO
     */
    public <E, Q> PageDTO<E> pageQuery(
            QueryPage<Q> query,
            BiFunction<Specification<E>, Pageable, Page<E>> findPage) {
        Pageable pageable= Pageable.ofSize(Math.toIntExact(query.getSize()))
                .withPage(Math.toIntExact(query.getCurrent()));
        Specification<E> specification = specification(query.getQuery());
        Page<E> page= findPage.apply(specification, pageable);
        List<E> items = page.getContent();
        return new PageDTO<>(query.getCurrent(), (long) page.getTotalPages(), query.getSize(), items);
    }


    public <E, Q> PageDTO<E> pageQuery(
            QueryPage<Q> query,
            JpaSpecificationExecutor<E> executor) {
        return this.pageQuery( query, executor::findAll);
    }
    /**
     * 根据符合规则的查询对象自动生成简单Specification
     * 对象内字段属性不是QueryField<?>的,会自动转换为QueryField<?>对象
     * 需要join的条件需要添加JoinQuery
     *
     * @param queryParams
     * @param <T>
     * @return
     */
    public <T> Specification<T> specification(Object queryParams) {
        if (null == queryParams) {
            return (root, query, criteriaBuilder) -> query.getRestriction();
        }
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
                    for (Object v : (Collection<?>) value) {
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
            if (predicate==null){
                return query.orderBy(orders).getRestriction();
            }
            return query.orderBy(orders).where(predicate).getRestriction();
        };
    }

    private <T> Predicate getPredicate(Root<T> root, CriteriaBuilder criteriaBuilder, List<Order> orders, Map<String, Path<T>> cacheJoin, Field declaredField, Object value, String column) {
        QueryField<?> queryField = getQueryField(value);
        QueryFilter queryFilter = declaredField.getAnnotation(QueryFilter.class);
        if (queryFilter != null) {
            QueryFilterType[] types = queryFilter.types();
            boolean supportType = Arrays.stream(types).anyMatch(type -> type == queryField.getType());
            Preconditions.checkArgument(supportType, "filterType not support");
        }
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
                return likePredicate(column, root, cb, key, StringConstants.PERCENT + value.toString() + StringConstants.PERCENT);
            case STARTS_WITH:
                return likePredicate(column, root, cb, key, value.toString() + StringConstants.PERCENT);
            case ENDS_WITH:
                return likePredicate(column, root, cb, key, StringConstants.PERCENT + value.toString());
            case EQUALS:
                return cb.equal(root.get(column), Strings.isNullOrEmpty(key) ? value : encrypt(value, key));
            case NOT_EQUALS:
                return cb.notEqual(root.get(column), Strings.isNullOrEmpty(key) ? value : encrypt(value, key));
            case CONTAINS:
                return containsPredicate(column, root, key, value, null);
            case NOT_CONTAINS:
                return containsPredicate(column, root, key, value, Predicate::not);
            case LESSTHAN:
                return comparablePredicate(root.get(column), value, cb::lessThan);
            case LESSTHANEQUAL:
                return comparablePredicate(root.get(column), value, cb::lessThanOrEqualTo);
            case GREATERTHANE:
                return comparablePredicate(root.get(column), value, cb::greaterThan);
            case GREATERTHANEQUAL:
                return comparablePredicate(root.get(column), value, cb::greaterThanOrEqualTo);
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

    private <T> Predicate containsPredicate(String column, Path<T> root, String key, Object value, Function<Predicate, Predicate> predicateFunction) {
        Predicate predicate;
        if (value instanceof Collection) {
            if (Strings.isNullOrEmpty(key)) {
                predicate = root.get(column).in(((Collection<?>) value).toArray());
            } else {
                Object[] values = ((Collection<?>) value).stream().map(v -> encrypt(v, key)).toArray();
                predicate = root.get(column).in(values);
            }
        } else {
            predicate = root.get(column).in(Strings.isNullOrEmpty(key) ? value : encrypt(value, key));
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
        if (Strings.isNullOrEmpty(key)) {
            return cb.like(root.get(column), pattern);
        }
        CriteriaBuilderImpl criteriaBuilder = (CriteriaBuilderImpl) cb;
        Expression<String> expression = new AesDecryptFunction(criteriaBuilder, new UnHexFunction(criteriaBuilder, root.get(column)), key);
        return cb.like(expression, pattern);
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


    private String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
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
            throw new IllegalArgumentException("加密异常,值必须为String类型");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {

            throw new RuntimeException("加密异常", e);
        }

    }

}
