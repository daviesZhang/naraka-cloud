package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.QueryPage;
import com.davies.naraka.autoconfigure.QueryUtils;
import com.davies.naraka.autoconfigure.annotation.ColumnName;
import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Specification查询构造工具类
 * 支持多表链接,支持查询加密数据
 *
 * @author sycho
 * @see JoinQuery
 * @see QueryField
 */
@Slf4j
public class JpaSpecificationUtils extends QueryUtils {


    @Getter
    @Setter
    private List<JpaQueryHandle> queryHandleList;

    public JpaSpecificationUtils() {

    }

    public JpaSpecificationUtils(List<JpaQueryHandle> queryHandleList) {
        this.queryHandleList = queryHandleList;
    }


    /**
     * 分页获取数据
     *
     * @param query    查询参数
     * @param findPage 分页查询的实际方法
     * @param supplier 需要转换对象的实例获取方法
     * @param <T>      返回的DTO
     * @param <E>      entity
     * @param <Q>      查询对象
     * @return PageDTO
     */
    public <T, E, Q> PageDTO<T> pageQuery(
            QueryPage<Q> query,
            BiFunction<Specification<E>, Pageable, Page<E>> findPage,
            Supplier<T> supplier) {
        Pageable pageable = Pageable.ofSize(Math.toIntExact(query.getSize()))
                .withPage(Math.toIntExact(query.getCurrent()));
        Specification<E> specification = specification(query.getQuery());
        Page<T> page = findPage.apply(specification, pageable).map(e -> ClassUtils.copyObject(e, supplier.get()));
        List<T> items = page.getContent();
        return new PageDTO<>(query.getCurrent(), (long) page.getTotalPages(), query.getSize(), items);
    }

    public <T, E, Q> PageDTO<T> pageQuery(
            QueryPage<Q> query,
            JpaSpecificationExecutor<E> executor,
            Supplier<T> supplier) {
        return this.pageQuery(query, (BiFunction<Specification<E>, Pageable, Page<E>>) executor::findAll, supplier);
    }


    /**
     * 分页获取数据
     *
     * @param query    查询参数
     * @param findPage 分页查询的实际方法
     * @param <E>      entity
     * @param <Q>      查询对象
     * @return PageDTO
     */
    public <E, Q> PageDTO<E> pageQuery(
            QueryPage<Q> query,
            BiFunction<Specification<E>, Pageable, Page<E>> findPage) {
        Pageable pageable = Pageable.ofSize(Math.toIntExact(query.getSize()))
                .withPage(Math.toIntExact(query.getCurrent()));
        Specification<E> specification = specification(query.getQuery());
        Page<E> page = findPage.apply(specification, pageable);
        List<E> items = page.getContent();
        return new PageDTO<>(query.getCurrent(), (long) page.getTotalPages(), query.getSize(), items);
    }


    public <E, Q> PageDTO<E> pageQuery(
            QueryPage<Q> query,
            JpaSpecificationExecutor<E> executor) {
        return this.pageQuery(query, executor::findAll);
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
        return (root, query, criteriaBuilder) -> this.toPredicate(queryParams, root, query, criteriaBuilder);
    }

    /**
     * //todo 支持map,支持@QueryConfig
     *
     * @param queryParams
     * @param root
     * @param query
     * @param criteriaBuilder
     * @param <T>
     * @return
     */
    private <T> Predicate toPredicate(Object queryParams, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Class<?> classes = queryParams.getClass();
        Field[] fields = classes.getDeclaredFields();
        Predicate predicate = null;
        List<Order> orders = Lists.newArrayList();
        Map<String, Path<T>> cacheJoin = new HashMap<>(3);
        for (Field declaredField : fields) {

            Optional<Object> valueOptional = queryValue(queryParams, declaredField);
            if (!valueOptional.isPresent()) {
                continue;
            }
            Object value = valueOptional.get();
            String column = declaredField.getName();
            ColumnName columnName = declaredField.getDeclaredAnnotation(ColumnName.class);
            if (columnName != null && !Strings.isNullOrEmpty(columnName.value())) {
                column = columnName.value();
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
        if (predicate == null) {
            return query.orderBy(orders).getRestriction();
        }
        return query.orderBy(orders).where(predicate).getRestriction();
    }

    private <T> Predicate getPredicate(Root<T> root, CriteriaBuilder cb, List<Order> orders, Map<String, Path<T>> cacheJoin, Field field, Object value, String column) {
        QueryField<?> queryField = getQueryField(value);
        checkFilterType(queryField, field);
        JoinQuery joinQuery = field.getDeclaredAnnotation(JoinQuery.class);
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
                cb, field);
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
            Field field) {
        Object value = queryField.getFilter();
        QueryFilterType queryFilterType = queryField.getType();
        List<JpaQueryHandle> handleList = getQueryHandleList();
        List<JpaQueryHandle> list;
        if (handleList == null || handleList.isEmpty()) {
            list = Lists.newArrayList();
        } else {
            list = Lists.newArrayList(handleList);
        }
        list.add(new DefaultJpaQueryHandle());
        for (JpaQueryHandle queryHandle : list) {
            boolean support = queryHandle.support(queryFilterType, value, column, field);
            if (support) {
                return queryHandle.handle(queryFilterType, value, orders, column, root, cb, field);
            }
        }
        throw new IllegalArgumentException(Strings.lenientFormat("%s 不支持这个运算符", queryFilterType));


    }


}
